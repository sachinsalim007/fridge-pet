import { onRequest } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import * as logger from "firebase-functions/logger";
import { z } from "zod";
import {
  extractReceiptOrFridgeItems,
  generatePetDialogue,
  generateRescueRecipes,
} from "./gemini";

const geminiApiKey = defineSecret("GEMINI_API_KEY");

const ParseReceiptRequestSchema = z.object({
  imageBase64: z.string().min(16),
  mimeType: z.enum(["image/jpeg", "image/png", "image/webp"]).default("image/jpeg"),
  scanMode: z.enum(["receipt", "fridge"]).default("receipt"),
});

const RescueRecipesRequestSchema = z.object({
  petName: z.string().min(1).max(40).default("Brrbie"),
  petState: z.string().min(1).max(30).default("ANXIOUS"),
  expiringItems: z
    .array(
      z.object({
        name: z.string().min(1),
        daysUntilExpiry: z.number().int(),
        shelf: z.string().default("FRIDGE"),
        quantityLabel: z.string().default("1 unit"),
      })
    )
    .min(1)
    .max(25),
});

const PetDialogueRequestSchema = z.object({
  petName: z.string().min(1).max(40),
  petState: z.string().min(1).max(30),
  evolutionStage: z.number().int().min(1).max(3).default(1),
  expiringSoonNames: z.array(z.string()).max(10).default([]),
  expiredCount: z.number().int().min(0).default(0),
  totalItems: z.number().int().min(0).default(0),
  savedAedThisWeek: z.number().min(0).default(0),
});

export const parseReceipt = onRequest(
  {
    region: "me-central1",
    timeoutSeconds: 60,
    memory: "512MiB",
    secrets: [geminiApiKey],
    cors: true,
  },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({ error: "Method not allowed" });
      return;
    }

    const parsed = ParseReceiptRequestSchema.safeParse(req.body);
    if (!parsed.success) {
      res.status(400).json({ error: "Invalid request payload", details: parsed.error.flatten() });
      return;
    }

    try {
      const result = await extractReceiptOrFridgeItems(
        geminiApiKey.value(),
        parsed.data.imageBase64,
        parsed.data.mimeType,
        parsed.data.scanMode
      );
      res.status(200).json(result);
    } catch (error) {
      logger.error("parseReceipt failed", error);
      res.status(500).json({ error: "Failed to analyze receipt image" });
    }
  }
);

export const suggestRescueRecipes = onRequest(
  {
    region: "me-central1",
    timeoutSeconds: 45,
    memory: "256MiB",
    secrets: [geminiApiKey],
    cors: true,
  },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({ error: "Method not allowed" });
      return;
    }

    const parsed = RescueRecipesRequestSchema.safeParse(req.body);
    if (!parsed.success) {
      res.status(400).json({ error: "Invalid request payload", details: parsed.error.flatten() });
      return;
    }

    try {
      const result = await generateRescueRecipes(
        geminiApiKey.value(),
        parsed.data.petName,
        parsed.data.petState,
        parsed.data.expiringItems
      );
      res.status(200).json(result);
    } catch (error) {
      logger.error("suggestRescueRecipes failed", error);
      res.status(500).json({ error: "Failed to generate rescue recipes" });
    }
  }
);

export const getPetDialogue = onRequest(
  {
    region: "me-central1",
    timeoutSeconds: 20,
    memory: "256MiB",
    secrets: [geminiApiKey],
    cors: true,
  },
  async (req, res) => {
    if (req.method !== "POST") {
      res.status(405).json({ error: "Method not allowed" });
      return;
    }

    const parsed = PetDialogueRequestSchema.safeParse(req.body);
    if (!parsed.success) {
      res.status(400).json({ error: "Invalid request payload", details: parsed.error.flatten() });
      return;
    }

    try {
      const line = await generatePetDialogue(geminiApiKey.value(), parsed.data);
      res.status(200).json({ line });
    } catch (error) {
      logger.error("getPetDialogue failed", error);
      res.status(500).json({ error: "Failed to generate pet dialogue" });
    }
  }
);
