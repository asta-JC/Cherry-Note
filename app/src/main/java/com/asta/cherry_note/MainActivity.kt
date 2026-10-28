package com.asta.cherry_note
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.asta.cherry_note.data.*
import com.asta.cherry_note.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: CherryViewModel = viewModel()
            val appearance by vm.appearance.collectAsStateWithLifecycle()
            CherryNoteTheme(appearance) {
                CherryApp(vm)
            }
        }
    }
}
@Composable private fun muted() = MaterialTheme.colorScheme.onSurfaceVariant
@Composable private fun noteColor(i: Int) = noteBackground(i, LocalCherryDarkTheme.current)
@Composable fun CherryApp(vm: CherryViewModel) {
    val nav=rememberNavController()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val folders by vm.folders.collectAsStateWithLifecycle()
    val entry by nav.currentBackStackEntryAsState()
    val route=entry?.destination?.route ?: "home"
    Scaffold(containerColor=MaterialTheme.colorScheme.background, bottomBar= {
        if(!route.startsWith("editor")) NavigationBar(containerColor=MaterialTheme.colorScheme.surface,tonalElevation=0.dp) {
            listOf(Triple("home","Notes",Icons.Outlined.Description),Triple("collections","Collections",Icons.Outlined.FolderOpen),Triple("settings","Settings",Icons.Outlined.Tune)).forEach {
                (path,title,icon) -> NavigationBarItem(selected=route==path,onClick= {
                    // Read the controller directly so rapid taps cannot reuse a stale route.
                    if (nav.currentDestination?.route != path) {
                        nav.navigate(path) {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    }
                }
                ,icon= {
                    Icon(icon,title)
                }
                ,label= {
                    Text(title,fontSize=11.sp)
                }
                ,colors=NavigationBarItemDefaults.colors(indicatorColor=MaterialTheme.colorScheme.primary.copy(alpha=.1f)))
            }
        }
    }
    ,floatingActionButton= {
        if(route=="home") ExtendedFloatingActionButton(onClick= {
            val n=Note()
            vm.save(n) { nav.navigate("editor/${n.id}") }
        }
        ,containerColor=MaterialTheme.colorScheme.primary,contentColor=MaterialTheme.colorScheme.onPrimary,shape=RoundedCornerShape(22.dp),icon= {
            Icon(Icons.Outlined.Add,null)
        }
        ,text= {
            Text("New note")
        }
        )
    }
    ) {
        padding ->
        NavHost(nav,"home",Modifier.padding(padding).background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha=.035f), MaterialTheme.colorScheme.background))),enterTransition= {
            fadeIn()+slideInHorizontally {
                it/15
            }
        }
        ,exitTransition= {
            fadeOut()
        }
        ) {
            composable("home") {
                Home(notes, folders) { nav.navigate("editor/$it") }
            }
            composable("collections") {
                Collections(folders,notes,vm) {
                    nav.navigate("editor/$it")
                }
            }
            composable("settings") {
                Settings(vm)
            }
            composable("editor/{id}") {
                back -> notes.find {
                    it.id==back.arguments?.getString("id")
                }
                ?.let {
                    Editor(it,folders,vm) {
                        nav.popBackStack()
                    }
                }
            }
        }
    }
}
@Composable private fun CherryArt(modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val x=size.width/100
        val y=size.height/100
        drawCircle(Color(0xFFECC5CF).copy(alpha=.3f),size.minDimension*.46f,Offset(50*x,53*y))
        val path=Path().apply {
            moveTo(31*x,65*y)
            cubicTo(37*x,39*y,58*x,44*y,61*x,12*y)
            moveTo(70*x,69*y)
            cubicTo(76*x,43*y,60*x,42*y,61*x,12*y)
        }
        drawPath(path,Color(0xFF74805C),style=androidx.compose.ui.graphics.drawscope.Stroke(3*x,cap=StrokeCap.Round))
        drawOval(Color(0xFF849166),Offset(62*x,14*y),androidx.compose.ui.geometry.Size(24*x,10*y))
        drawCircle(Brush.linearGradient(listOf(Color(0xFFE36581),Color(0xFF96183F))),19*x,Offset(31*x,70*y))
        drawCircle(Brush.linearGradient(listOf(Color(0xFFEF8BA0),Color(0xFFA2244B))),20*x,Offset(70*x,75*y))
        drawCircle(Color.White.copy(alpha=.4f),3*x,Offset(25*x,62*y))
        drawCircle(Color.White.copy(alpha=.4f),3*x,Offset(64*x,66*y))
    }
}
@Composable private fun Brand() {
    Row(verticalAlignment=Alignment.CenterVertically) {
        CherryArt(Modifier.size(34.dp))
        Spacer(Modifier.width(6.dp))
        Text("cherry note",fontFamily=FontFamily.Serif,fontSize=23.sp,fontWeight=FontWeight.Bold,letterSpacing=(-.7).sp)

    }
}
internal fun matches(note:Note,query:String)=query.isBlank() || listOf(note.title,note.body,note.tags).any {
    it.contains(query.trim(),true)
}
@Composable private fun Home(notes: List<Note>, folders: List<Folder>, open: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All notes") }
    val visible = notes.filter { (category == "All notes" || it.folder == category) && matches(it, query) }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(155.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalItemSpacing = 12.dp,
        contentPadding = PaddingValues(22.dp, 18.dp, 22.dp, 105.dp), modifier = Modifier.fillMaxSize()
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Column {
                Brand()
                Spacer(Modifier.height(22.dp))
                Text("Your notes", fontFamily = FontFamily.Serif, fontSize = 32.sp)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search notes", fontSize = 15.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Outlined.Close, "Clear search") }
                    },
                    singleLine = true, shape = RoundedCornerShape(17.dp)
                )
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All notes") + folders.map { it.name }) { name ->
                        FilterChip(
                            selected = category == name, onClick = { category = name },
                            label = { Text(name, fontSize = 12.sp) }, shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
        if (visible.isEmpty()) item(span = StaggeredGridItemSpan.FullLine) {
            EmptyState(if (query.isBlank()) "No notes yet" else "No notes found",
                if (query.isBlank()) "Tap New note to start writing." else "Try another word or clear the search.")
        }
        val pinned = visible.filter { it.pinned }
        val other = visible.filterNot { it.pinned }
        if (pinned.isNotEmpty()) item(span = StaggeredGridItemSpan.FullLine) {
            SectionLabel("Pinned", "${pinned.size}", Icons.Outlined.PushPin)
        }
        items(pinned, key = { it.id }) { NoteCard(it, open) }
        if (other.isNotEmpty()) item(span = StaggeredGridItemSpan.FullLine) {
            SectionLabel(if (query.isBlank()) "Notes" else "Results", "${other.size}", Icons.AutoMirrored.Outlined.Notes)
        }
        items(other, key = { it.id }) { NoteCard(it, open) }
    }
}
@Composable private fun SectionLabel(title:String,count:String,icon:ImageVector) {
    Row(Modifier.fillMaxWidth().padding(bottom=4.dp),verticalAlignment=Alignment.CenterVertically) {
        Icon(icon,null,Modifier.size(15.dp),tint=MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(7.dp))
        Text(title,fontSize=14.sp,fontWeight=FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        Text(count,fontSize=10.sp,color=muted())
    }
}
@Composable private fun NoteCard(note:Note,open:(String)->Unit) {
    Surface(onClick= {
        open(note.id)
    }
    ,color=noteColor(note.color),shape=RoundedCornerShape(19.dp),border=BorderStroke(.7.dp,MaterialTheme.colorScheme.onSurface.copy(alpha=.04f)),modifier=Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CherryArt(Modifier.size(28.dp))
                Spacer(Modifier.width(6.dp))
                Text(note.folder.uppercase(),fontSize=8.sp,letterSpacing=1.3.sp,color=muted(),modifier=Modifier.weight(1f))
                if(note.pinned)Icon(Icons.Outlined.PushPin,"Pinned",Modifier.size(13.dp),tint=MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(13.dp))
            Text(note.title.ifBlank {
                "Untitled thought"
            }
            ,fontFamily=FontFamily.Serif,fontSize=21.sp,lineHeight=25.sp)
            Spacer(Modifier.height(10.dp))
            Text(note.body,fontSize=12.sp,lineHeight=20.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.77f),maxLines=7,overflow=TextOverflow.Ellipsis)
            if(note.tags.isNotBlank()) {
                Spacer(Modifier.height(15.dp))
                Text("#"+note.tags.split(',').first().trim().replace(" ",""),fontSize=9.sp,color=MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(18.dp))
            Row {
                val currentLocale = LocalConfiguration.current.locales[0]
                Text(remember(currentLocale) { SimpleDateFormat("MMM d", currentLocale) }.format(Date(note.updated)),fontSize=9.sp,color=muted())
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
@Composable private fun EmptyState(title:String,body:String) {
    Column(Modifier.fillMaxWidth().padding(vertical=35.dp),horizontalAlignment=Alignment.CenterHorizontally) {
        CherryArt(Modifier.size(110.dp))
        Text(title,fontFamily=FontFamily.Serif,fontSize=25.sp)
        Spacer(Modifier.height(12.dp))
        Text(body,color=muted(),textAlign=androidx.compose.ui.text.style.TextAlign.Center,fontSize=14.sp)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun Editor(initial: Note, folders: List<Folder>, vm: CherryViewModel, back: () -> Unit) {
    var note by remember(initial.id) { mutableStateOf(initial) }
    var delete by remember { mutableStateOf(false) }
    var options by rememberSaveable { mutableStateOf(false) }
    var folderMenu by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(true) }
    val focus = androidx.compose.ui.platform.LocalFocusManager.current
    fun update(next: Note) {
        val updated = next.copy(updated = System.currentTimeMillis())
        note = updated
        saved = false
        vm.save(updated) { if (note == updated) saved = true }
    }
    fun finish() { focus.clearFocus(); back() }
    BackHandler { finish() }
    Column(Modifier.fillMaxSize().imePadding().background(noteColor(note.color))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { finish() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Done")
            }
            Spacer(Modifier.weight(1f))
            Text(if (saved) "Saved" else "Saving…", fontSize = 12.sp, color = muted())
            IconButton(onClick = { focus.clearFocus(); options = true }) { Icon(Icons.Outlined.MoreVert, "Note options") }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(16.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = note.title, onValueChange = { update(note.copy(title = it)) },
                textStyle = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Serif),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).semantics { contentDescription = "Note title" },
                decorationBox = { inner ->
                    if (note.title.isBlank()) Text("Title", fontFamily = FontFamily.Serif, fontSize = 32.sp, color = muted())
                    inner()
                }
            )
            Spacer(Modifier.height(16.dp))
            if (note.checklist) {
                note.body.split('\n').forEachIndexed { index, line ->
                    val checked = line.startsWith("☑")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = checked, onCheckedChange = { value ->
                            val lines = note.body.split('\n').toMutableList()
                            lines[index] = (if (value) "☑ " else "☐ ") + line.removePrefix("☑ ").removePrefix("☐ ")
                            update(note.copy(body = lines.joinToString("\n")))
                        }, modifier = Modifier.semantics { contentDescription = "Complete item ${index + 1}" })
                        androidx.compose.foundation.text.BasicTextField(
                            value = line.removePrefix("☑ ").removePrefix("☐ "),
                            onValueChange = { value ->
                                val lines = note.body.split('\n').toMutableList()
                                lines[index] = (if (checked) "☑ " else "☐ ") + value
                                update(note.copy(body = lines.joinToString("\n")))
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (note.bold) FontWeight.Bold else FontWeight.Normal,
                                textDecoration = if (checked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            ), singleLine = true,
                            modifier = Modifier.weight(1f).padding(vertical = 12.dp).semantics { contentDescription = "Item ${index + 1}" }
                        )
                        IconButton(onClick = {
                            val lines = note.body.split('\n').toMutableList()
                            lines.removeAt(index)
                            update(note.copy(body = lines.joinToString("\n")))
                        }) { Icon(Icons.Outlined.Close, "Remove item ${index + 1}", Modifier.size(16.dp)) }
                    }
                }
                TextButton(onClick = { update(note.copy(body = if (note.body.isBlank()) "☐ " else note.body + "\n☐ ")) }) {
                    Icon(Icons.Outlined.Add, null, Modifier.size(18.dp))
                    Text("Add item", Modifier.padding(start = 8.dp))
                }
            } else {
                androidx.compose.foundation.text.BasicTextField(
                    value = note.body, onValueChange = { update(note.copy(body = it)) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface, lineHeight = 28.sp,
                        fontWeight = if (note.bold) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 280.dp).semantics { contentDescription = "Note body" },
                    decorationBox = { inner ->
                        if (note.body.isBlank()) Text("Start writing…", color = muted(), style = MaterialTheme.typography.bodyLarge)
                        inner()
                    }
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
    if (options) ModalBottomSheet(onDismissRequest = { options = false }) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Note options", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                TextButton(onClick = { options = false }) { Text("Done") }
            }
            Spacer(Modifier.height(16.dp))
            Text("Color", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            NoteColorPicker(note.color) { update(note.copy(color = it)) }
            Spacer(Modifier.height(20.dp))
            NoteToggle("Pin to top", note.pinned) { update(note.copy(pinned = it)) }
            NoteToggle("Checklist", note.checklist) { update(note.copy(checklist = it)) }
            NoteToggle("Bold text", note.bold) { update(note.copy(bold = it)) }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Box {
                TextButton(onClick = { folderMenu = true }) {
                    Icon(Icons.Outlined.FolderOpen, null, Modifier.size(18.dp))
                    Text("Collection: ${note.folder}", Modifier.padding(horizontal = 8.dp))
                    Icon(Icons.Outlined.ExpandMore, null, Modifier.size(18.dp))
                }
                DropdownMenu(folderMenu, { folderMenu = false }) {
                    folders.forEach { folder ->
                        DropdownMenuItem(text = { Text(folder.name) }, onClick = {
                            update(note.copy(folder = folder.name)); folderMenu = false
                        })
                    }
                }
            }
            OutlinedTextField(
                note.tags, { update(note.copy(tags = it)) }, label = { Text("Tags (optional)") },
                placeholder = { Text("work, ideas") }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp)
            )
            TextButton(onClick = { options = false; delete = true }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.Outlined.DeleteOutline, null, Modifier.size(18.dp))
                Text("Delete note", Modifier.padding(start = 8.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
    if (delete) AlertDialog(
        onDismissRequest = { delete = false }, title = { Text("Delete note?") },
        text = { Text("This note will be permanently deleted.") },
        confirmButton = { TextButton(onClick = { vm.delete(note); delete = false; back() }) { Text("Delete") } },
        dismissButton = { TextButton(onClick = { delete = false }) { Text("Cancel") } }
    )
}

@Composable private fun NoteToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked, onChange, modifier = Modifier.semantics { contentDescription = label })
    }
}

@Composable private fun NoteColorPicker(selected: Int, onSelect: (Int) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(NoteColorNames.indices.toList()) { index ->
            Surface(
                onClick = { onSelect(index) }, color = noteColor(index), shape = CircleShape,
                border = BorderStroke(if (selected == index) 2.dp else 1.dp,
                    if (selected == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.size(48.dp).semantics {
                    contentDescription = "${NoteColorNames[index]} color"
                    this.selected = selected == index
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (selected == index) Icon(Icons.Outlined.Check, null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable private fun Collections(folders: List<Folder>, notes: List<Note>, vm: CherryViewModel, open: (String) -> Unit) {
    var adding by remember { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var icon by rememberSaveable { mutableStateOf("✦") }
    var color by rememberSaveable { mutableStateOf(0) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val folderName = selected
    if (folderName != null) {
        BackHandler { selected = null }
        Column(Modifier.fillMaxSize().padding(horizontal = 22.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selected = null }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back to collections") }
                Text(folderName, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            }
            Button(onClick = {
                val note = Note(folder = folderName)
                vm.save(note) { open(note.id) }
            }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Outlined.Add, null)
                Text("New note", Modifier.padding(start = 8.dp))
            }
            val collectionNotes = notes.filter { it.folder == folderName }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(155.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp, contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (collectionNotes.isEmpty()) item(span = StaggeredGridItemSpan.FullLine) {
                    EmptyState("No notes yet", "Tap New note to add one to this collection.")
                }
                items(collectionNotes, key = { it.id }) { NoteCard(it, open) }
            }
        }
        return
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        Brand()
        Spacer(Modifier.height(22.dp))
        Text("Collections", fontFamily = FontFamily.Serif, fontSize = 32.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { adding = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Outlined.CreateNewFolder, null, Modifier.size(20.dp))
            Text("New collection", Modifier.padding(start = 8.dp))
        }
        Spacer(Modifier.height(12.dp))
        folders.forEach { folder ->
            Surface(onClick = { selected = folder.name }, color = noteColor(folder.color), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(folder.icon, fontSize = 26.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(folder.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("${notes.count { it.folder == folder.name }} notes", fontSize = 12.sp, color = muted())
                    }
                    Icon(Icons.Outlined.ChevronRight, null, tint = muted())
                }
            }
        }
    }
    if (adding) AlertDialog(
        onDismissRequest = { adding = false }, title = { Text("New collection") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true)
                Spacer(Modifier.height(16.dp))
                Text("Icon", style = MaterialTheme.typography.labelLarge)
                Row {
                    listOf("✦", "♡", "☾", "↗").forEach { symbol ->
                        FilterChip(selected = icon == symbol, onClick = { icon = symbol }, label = { Text(symbol) }, modifier = Modifier.padding(end = 8.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Color", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(10.dp))
                NoteColorPicker(color) { color = it }
            }
        },
        confirmButton = {
            TextButton(enabled = name.trim().isNotEmpty() && folders.none { it.name.equals(name.trim(), true) }, onClick = {
                vm.folder(Folder(name.trim(), icon, color)); name = ""; adding = false
            }) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = { adding = false }) { Text("Cancel") } }
    )
}

@Composable private fun Settings(vm:CherryViewModel) {
    val appearance by vm.appearance.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp)) {
        Brand()
        Spacer(Modifier.height(30.dp))
        Text("Settings",fontFamily=FontFamily.Serif,fontSize=32.sp)
        Spacer(Modifier.height(8.dp))
        Text("Choose how your notes look.",color=muted(),fontSize=14.sp)
        Spacer(Modifier.height(30.dp))
        Preference("Appearance","Set the mood",listOf("Light","Dark","System"),appearance.theme) {
            vm.preference("theme",it)
        }
        Preference("Typography","Find your voice",listOf("Modern","Literary"),appearance.type) {
            vm.preference("type",it)
        }
        Text("Notes save automatically on this device.", color = muted(), fontSize = 13.sp)
        Spacer(Modifier.height(26.dp))
        Text("CHERRY NOTE  /  VERSION 1.0",fontSize=9.sp,letterSpacing=2.sp,color=muted())
        Spacer(Modifier.height(25.dp))
    }
}
@Composable private fun Preference(title:String,subtitle:String,options:List<String>,selected:String,onSelect:(String)->Unit) {
    Text(title,fontWeight=FontWeight.SemiBold,fontSize=17.sp)
    Text(subtitle,color=muted(),fontSize=12.sp,modifier=Modifier.padding(top=4.dp,bottom=12.dp))
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        options.forEach {
            option->FilterChip(selected=selected==option,onClick= {
                onSelect(option)
            }
            ,label= {
                Text(option)
            }
            ,leadingIcon= {
                if(selected==option)Icon(Icons.Outlined.Check,null,Modifier.size(16.dp))
            }
            )
        }
    }
    Spacer(Modifier.height(25.dp))
}
