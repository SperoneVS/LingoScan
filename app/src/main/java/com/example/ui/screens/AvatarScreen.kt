package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AvatarView
import com.example.ui.model.AvatarCategory
import com.example.ui.model.AvatarCustomization
import com.example.ui.model.SuitId
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarScreen(
    customization: AvatarCustomization,
    totalStars: Int,
    onSaveAvatar: (AvatarCustomization) -> Unit,
    onUpdateUsername: (String) -> Unit,
    onSelectSuit: (SuitId) -> Unit,
    onUnlockSuit: (SuitId) -> Boolean,
    onUpdateCategoryOption: (AvatarCategory, Int) -> Unit,
    onUpdateCategoryColor: (AvatarCategory, Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = SUITS, 1 = AVATAR
    var selectedCategory by remember { mutableStateOf(AvatarCategory.SKIN_TONE) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var usernameInput by remember(customization.username) { mutableStateOf(customization.username) }

    // Dialog for changing username
    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✏️ Benutzername wählen")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Dein Name wird im Vokabeltraining, der Statistik und auf deinen Zertifikaten angezeigt!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { if (it.length <= 20) usernameInput = it },
                        label = { Text("Benutzername") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateUsername(usernameInput)
                        showUsernameDialog = false
                    },
                    modifier = Modifier.testTag("save_username_button")
                ) {
                    Text("Speichern")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUsernameDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Avatar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("avatar_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onSaveAvatar(customization)
                            onClose()
                        },
                        modifier = Modifier.testTag("avatar_done_button")
                    ) {
                        Text(
                            text = "DONE",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFF9DB)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF131F24)) // Modern dark slate bottom, like Duolingo
        ) {
            // -------------------------------------------------------------
            // UPPER HALF: LIVE AVATAR PREVIEW (Dynamic customizable stage)
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(customization.currentBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Avatar View with background & scenery
                    AvatarView(
                        customization = customization,
                        size = 190.dp,
                        showBackground = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Username pill & Star badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.9f),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .clickable { showUsernameDialog = true }
                                .testTag("username_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = customization.username,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF2C2F38)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Name ändern",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Stars pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFD54F),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐ $totalStars", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF3E2723))
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // MAIN TABS: SUITS vs AVATAR
            // -------------------------------------------------------------
            TabRow(
                selectedTabIndex = selectedMainTab,
                containerColor = Color(0xFF131F24),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                        color = Color(0xFF26C6DA), // Vibrant cyan accent
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    text = {
                        Text(
                            text = "SUITS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedMainTab == 0) Color(0xFF26C6DA) else Color(0xFF78909C)
                        )
                    },
                    modifier = Modifier.testTag("tab_suits")
                )
                Tab(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    text = {
                        Text(
                            text = "AVATAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedMainTab == 1) Color(0xFF26C6DA) else Color(0xFF78909C)
                        )
                    },
                    modifier = Modifier.testTag("tab_avatar")
                )
            }

            // -------------------------------------------------------------
            // LOWER HALF CONTENT
            // -------------------------------------------------------------
            if (selectedMainTab == 0) {
                // SUITS TAB CONTENT
                SuitsTabContent(
                    customization = customization,
                    totalStars = totalStars,
                    onSelectSuit = onSelectSuit,
                    onUnlockSuit = { suit ->
                        val success = onUnlockSuit(suit)
                        if (success) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("🎉 '${suit.title}' freigeschaltet und angezogen!")
                            }
                        } else {
                            val needed = suit.starCost - totalStars
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Noch $needed ⭐ benötigt! Absolviere Quizzes, um Sterne zu sammeln.")
                            }
                        }
                    }
                )
            } else {
                // AVATAR TAB CONTENT
                AvatarTabContent(
                    customization = customization,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    onSelectOption = { category, index ->
                        onUpdateCategoryOption(category, index)
                    },
                    onSelectColor = { category, colorIndex ->
                        onUpdateCategoryColor(category, colorIndex)
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SUITS TAB: Grid of suits matching screenshot
// -------------------------------------------------------------
@Composable
private fun SuitsTabContent(
    customization: AvatarCustomization,
    totalStars: Int,
    onSelectSuit: (SuitId) -> Unit,
    onUnlockSuit: (SuitId) -> Unit
) {
    val suits = SuitId.entries

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("suits_grid"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(suits) { suit ->
            val isUnlocked = suit.id in customization.unlockedSuits || suit.starCost == 0
            val isSelected = customization.suitId == suit

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(0xFF26C6DA) else Color(0xFF2A3A42),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        if (isUnlocked) {
                            onSelectSuit(suit)
                        } else {
                            onUnlockSuit(suit)
                        }
                    }
                    .testTag("suit_card_${suit.id}"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A30)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Badge if any (e.g. EPIC, BELIEBT)
                        if (suit.badge != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (suit.badge == "EPIC") Color(0xFF9C27B0) else Color(0xFF26C6DA)
                            ) {
                                Text(
                                    text = suit.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Mini preview of avatar in this suit
                        AvatarView(
                            customization = customization.copy(suitId = suit),
                            size = 85.dp
                        )

                        // Title & Lock/Check status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (!isUnlocked) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Gesperrt",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${suit.starCost} ⭐",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            } else {
                                Text(
                                    text = suit.title.take(10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF26C6DA) else Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Checkmark badge if selected
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF26C6DA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Aktiv", tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// AVATAR TAB: Categories & Swatches matching screenshot
// -------------------------------------------------------------
@Composable
private fun AvatarTabContent(
    customization: AvatarCustomization,
    selectedCategory: AvatarCategory,
    onSelectCategory: (AvatarCategory) -> Unit,
    onSelectOption: (AvatarCategory, Int) -> Unit,
    onSelectColor: (AvatarCategory, Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Subcategory Icon Navigation Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A2A30))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val categories = AvatarCategory.entries
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF26C6DA).copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onSelectCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (cat) {
                                AvatarCategory.SKIN_TONE -> "👤"
                                AvatarCategory.EXPRESSION -> "👀"
                                AvatarCategory.HAIRSTYLE -> "💇"
                                AvatarCategory.GLASSES -> "👓"
                                AvatarCategory.FACIAL_HAIR -> "🧔"
                                AvatarCategory.HEADWEAR -> "🧢"
                                AvatarCategory.BACKGROUND -> "🖼️"
                            },
                            fontSize = 20.sp
                        )
                        Text(
                            text = cat.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF26C6DA) else Color(0xFF90A4AE)
                        )
                    }
                }
            }
        }

        // 2. Color Swatch Row (if category supports colors: Hair, Glasses, Headwear, Skin, Background)
        val colorList: List<Color> = when (selectedCategory) {
            AvatarCategory.SKIN_TONE -> AvatarCustomization.SKIN_COLORS
            AvatarCategory.HAIRSTYLE -> AvatarCustomization.HAIR_COLORS
            AvatarCategory.GLASSES -> AvatarCustomization.GLASSES_COLORS
            AvatarCategory.FACIAL_HAIR -> AvatarCustomization.HAIR_COLORS
            AvatarCategory.HEADWEAR -> AvatarCustomization.HEADWEAR_COLORS
            AvatarCategory.BACKGROUND -> AvatarCustomization.BACKGROUND_COLORS
            AvatarCategory.EXPRESSION -> emptyList()
        }

        val currentColorIndex = when (selectedCategory) {
            AvatarCategory.SKIN_TONE -> customization.skinToneIndex
            AvatarCategory.HAIRSTYLE -> customization.hairColorIndex
            AvatarCategory.GLASSES -> customization.glassesColorIndex
            AvatarCategory.FACIAL_HAIR -> customization.facialHairColorIndex
            AvatarCategory.HEADWEAR -> customization.headwearColorIndex
            AvatarCategory.BACKGROUND -> customization.backgroundIndex
            AvatarCategory.EXPRESSION -> 0
        }

        if (colorList.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "${selectedCategory.title} Farbe",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB0BEC5)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(colorList) { idx, color ->
                        val isSelected = idx == currentColorIndex
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF26C6DA) else Color(0x44FFFFFF),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectColor(selectedCategory, idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // 3. Variant Options Grid (Matching screenshot options cards)
        val optionCount = when (selectedCategory) {
            AvatarCategory.SKIN_TONE -> AvatarCustomization.SKIN_COLORS.size
            AvatarCategory.EXPRESSION -> 5 // Cheerful, Open Mouth/Tooth, Wink, Smile, Curious
            AvatarCategory.HAIRSTYLE -> 8 // Buzz, Side-part, Afro, Pompadour, Curl strand, Long...
            AvatarCategory.GLASSES -> 4 // None, Round, Geek, Sunglasses
            AvatarCategory.FACIAL_HAIR -> 4 // None, Mustache, Goatee, Beard
            AvatarCategory.HEADWEAR -> 5 // None, Cap, Beanie, Crown, Chef
            AvatarCategory.BACKGROUND -> com.example.ui.model.SceneryTheme.entries.size
        }

        val currentSelectedOptionIndex = when (selectedCategory) {
            AvatarCategory.SKIN_TONE -> customization.skinToneIndex
            AvatarCategory.EXPRESSION -> customization.expressionIndex
            AvatarCategory.HAIRSTYLE -> customization.hairStyleIndex
            AvatarCategory.GLASSES -> customization.glassesIndex
            AvatarCategory.FACIAL_HAIR -> customization.facialHairIndex
            AvatarCategory.HEADWEAR -> customization.headwearIndex
            AvatarCategory.BACKGROUND -> customization.sceneryIndex
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("avatar_options_grid"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(optionCount) { index ->
                val isSelected = index == currentSelectedOptionIndex

                // Clone customization with this variant to render mini preview in card!
                val previewCustomization = when (selectedCategory) {
                    AvatarCategory.SKIN_TONE -> customization.copy(skinToneIndex = index)
                    AvatarCategory.EXPRESSION -> customization.copy(expressionIndex = index)
                    AvatarCategory.HAIRSTYLE -> customization.copy(hairStyleIndex = index)
                    AvatarCategory.GLASSES -> customization.copy(glassesIndex = index)
                    AvatarCategory.FACIAL_HAIR -> customization.copy(facialHairIndex = index)
                    AvatarCategory.HEADWEAR -> customization.copy(headwearIndex = index)
                    AvatarCategory.BACKGROUND -> customization.copy(sceneryIndex = index)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color(0xFF26C6DA) else Color(0xFF2A3A42),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectOption(selectedCategory, index) }
                        .testTag("avatar_option_${selectedCategory.name}_$index"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2F36)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedCategory == AvatarCategory.BACKGROUND) {
                            val scenery = com.example.ui.model.SceneryTheme.entries.getOrNull(index) ?: com.example.ui.model.SceneryTheme.MINIMAL
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(scenery.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = scenery.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF26C6DA) else Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            AvatarView(
                                customization = previewCustomization,
                                size = 80.dp
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF26C6DA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
