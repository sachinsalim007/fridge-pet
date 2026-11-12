package com.fridgepet.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fridgepet.app.ui.theme.ApricotContainer
import com.fridgepet.app.ui.theme.ApricotWarn
import com.fridgepet.app.ui.theme.BerryContainer
import com.fridgepet.app.ui.theme.BerryError
import com.fridgepet.app.ui.theme.MintContainer
import com.fridgepet.app.ui.theme.SagePrimary

@Composable
fun FoodSafetyBanner(
    modifier: Modifier = Modifier,
    text: String = "AI expiry dates are estimates — always smell, inspect, and check before eating.",
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = SagePrimary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun ExpiryChip(
    daysUntilExpiry: Int,
    modifier: Modifier = Modifier,
) {
    val (bg, fg, label) = when {
        daysUntilExpiry < 0 -> Triple(
            BerryContainer,
            BerryError,
            "Expired ${-daysUntilExpiry}d ago",
        )
        daysUntilExpiry == 0 -> Triple(BerryContainer, BerryError, "Due today")
        daysUntilExpiry <= 2 -> Triple(ApricotContainer, ApricotWarn, "${daysUntilExpiry}d left")
        else -> Triple(MintContainer, SagePrimary, "${daysUntilExpiry}d left")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg,
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
