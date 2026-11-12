import { GoogleGenAI } from "@google/genai";
import { z } from "zod";
import {
  PET_DIALOGUE_SYSTEM_INSTRUCTION,
  RECEIPT_SYSTEM_INSTRUCTION,
  RECIPE_SYSTEM_INSTRUCTION,
} from "./prompts";

const MODEL_PRIMARY = "gemini-3.8-flash";
const MODEL_FAST = "gemini-3.5-flash-lite";

export const ExtractedItemSchema = z.object({
  cleanName: z.string(),
  rawLabel: z.string(),
  quantityLabel: z.string(),
  shelf: z.enum(["FRIDGE", "FREEZER", "PANTRY"]),
  shelfLifeDays: z.number().int().min(1).max(365),
  priceAed: z.number().min(0),
  storageTip: z.string(),
});

export const ParseReceiptResponseSchema = z.object({
  merchantName: z.string(),
  currency: z.string(),
  items: z.array(ExtractedItemSchema),
  safetyNote: z.string(),
});

export type ParseReceiptResponse = z.infer<typeof ParseReceiptResponseSchema>;

export const RescueRecipeSchema = z.object({
  title: z.string(),
  prepMinutes: z.number().int().min(1).max(180),
  usedIngredients: z.array(z.string()),
  extraStaples: z.array(z.string()),
  steps: z.array(z.string()),
  petCheerLine: z.string(),
});

export const RescueRecipesResponseSchema = z.object({
  recipes: z.array(RescueRecipeSchema),
});

export type RescueRecipesResponse = z.infer<typeof RescueRecipesResponseSchema>;

export const PetDialogueResponseSchema = z.object({
  line: z.string(),
});

const RECEIPT_JSON_SCHEMA = {
  type: "object",
  properties: {
    merchantName: { type: "string", description: "Store name or 'Fridge Photo'" },
    currency: { type: "string", description: "Currency code, typically AED" },
    items: {
      type: "array",
      items: {
        type: "object",
        properties: {
          cleanName: { type: "string", description: "Clean lowercase English food name" },
          rawLabel: { type: "string", description: "Original text from receipt or photo" },
          quantityLabel: { type: "string", description: "Unit/weight such as 1 kg, 500 g, 1 L" },
          shelf: { type: "string", enum: ["FRIDGE", "FREEZER", "PANTRY"] },
          shelfLifeDays: { type: "integer", minimum: 1, maximum: 365 },
          priceAed: { type: "number", minimum: 0 },
          storageTip: { type: "string", description: "Practical 1-sentence storage advice" },
        },
        required: [
          "cleanName",
          "rawLabel",
          "quantityLabel",
          "shelf",
          "shelfLifeDays",
          "priceAed",
          "storageTip",
        ],
      },
    },
    safetyNote: {
      type: "string",
      description: "Reminder that AI expiry dates are estimates and to check before eating",
    },
  },
  required: ["merchantName", "currency", "items", "safetyNote"],
};

const RECIPES_JSON_SCHEMA = {
  type: "object",
  properties: {
    recipes: {
      type: "array",
      items: {
        type: "object",
        properties: {
          title: { type: "string" },
          prepMinutes: { type: "integer", minimum: 1, maximum: 180 },
          usedIngredients: { type: "array", items: { type: "string" } },
          extraStaples: { type: "array", items: { type: "string" } },
          steps: { type: "array", items: { type: "string" } },
          petCheerLine: { type: "string" },
        },
        required: [
          "title",
          "prepMinutes",
          "usedIngredients",
          "extraStaples",
          "steps",
          "petCheerLine",
        ],
      },
    },
  },
  required: ["recipes"],
};

const DIALOGUE_JSON_SCHEMA = {
  type: "object",
  properties: {
    line: { type: "string", description: "Short 1-2 sentence dialogue line from the pet" },
  },
  required: ["line"],
};

function createClient(apiKey: string): GoogleGenAI {
  return new GoogleGenAI({ apiKey });
}

export async function extractReceiptOrFridgeItems(
  apiKey: string,
  imageBase64: string,
  mimeType: string,
  scanMode: "receipt" | "fridge"
): Promise<ParseReceiptResponse> {
  const ai = createClient(apiKey);
  const promptText =
    scanMode === "fridge"
      ? "Identify all visible perishable food items in this fridge/pantry photo, estimate their remaining shelf life in days, and return structured JSON."
      : "Extract all food grocery items from this receipt (which may mix Arabic and English shorthand with AED prices), clean up names, estimate shelf life in days, and return structured JSON.";

  const interaction = await ai.interactions.create({
    model: MODEL_PRIMARY,
    system_instruction: RECEIPT_SYSTEM_INSTRUCTION,
    store: false,
    input: [
      { type: "text", text: promptText },
      { type: "image", data: imageBase64, mime_type: mimeType },
    ],
    response_format: {
      type: "text",
      mime_type: "application/json",
      schema: RECEIPT_JSON_SCHEMA,
    },
  });

  const rawText = interaction.output_text ?? "{}";
  const parsed = ParseReceiptResponseSchema.parse(JSON.parse(rawText));
  return {
    ...parsed,
    safetyNote:
      parsed.safetyNote ||
      "AI expiry dates are estimates — always smell, inspect, and check before eating.",
  };
}

export interface ExpiringIngredientInput {
  name: string;
  daysUntilExpiry: number;
  shelf: string;
  quantityLabel: string;
}

export async function generateRescueRecipes(
  apiKey: string,
  petName: string,
  petState: string,
  expiringItems: ExpiringIngredientInput[]
): Promise<RescueRecipesResponse> {
  const ai = createClient(apiKey);
  const itemsText = expiringItems
    .map(
      (item) =>
        `- ${item.name} (${item.quantityLabel}, ${item.shelf}, ${item.daysUntilExpiry <= 0 ? "due today" : `expires in ${item.daysUntilExpiry} day(s)`})`
    )
    .join("\n");

  const interaction = await ai.interactions.create({
    model: MODEL_PRIMARY,
    system_instruction: RECIPE_SYSTEM_INSTRUCTION,
    store: false,
    input: `Pet name: ${petName} (mood: ${petState})\nUrgent ingredients to rescue:\n${itemsText}\n\nSuggest 2 to 3 fast rescue recipes prioritizing the soonest-expiring items.`,
    response_format: {
      type: "text",
      mime_type: "application/json",
      schema: RECIPES_JSON_SCHEMA,
    },
  });

  const rawText = interaction.output_text ?? '{"recipes":[]}';
  return RescueRecipesResponseSchema.parse(JSON.parse(rawText));
}

export interface PetDialogueInput {
  petName: string;
  petState: string;
  evolutionStage: number;
  expiringSoonNames: string[];
  expiredCount: number;
  totalItems: number;
  savedAedThisWeek: number;
}

export async function generatePetDialogue(
  apiKey: string,
  context: PetDialogueInput
): Promise<string> {
  const ai = createClient(apiKey);
  const summary = [
    `Pet name: ${context.petName}`,
    `Mood state: ${context.petState}`,
    `Evolution stage: ${context.evolutionStage}`,
    `Total active items: ${context.totalItems}`,
    `Expired items sitting in fridge: ${context.expiredCount}`,
    `Expiring within 2 days: ${context.expiringSoonNames.join(", ") || "none"}`,
    `Saved this week: AED ${context.savedAedThisWeek.toFixed(0)}`,
  ].join("\n");

  const interaction = await ai.interactions.create({
    model: MODEL_FAST,
    system_instruction: PET_DIALOGUE_SYSTEM_INSTRUCTION,
    store: false,
    input: `Write one in-character Fridge Pet line based on this pantry snapshot:\n${summary}`,
    response_format: {
      type: "text",
      mime_type: "application/json",
      schema: DIALOGUE_JSON_SCHEMA,
    },
  });

  const rawText = interaction.output_text ?? '{"line":"Everything looks chill in here!"}';
  return PetDialogueResponseSchema.parse(JSON.parse(rawText)).line;
}
