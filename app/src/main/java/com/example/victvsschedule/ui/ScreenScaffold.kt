package com.example.victvsschedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.victvsschedule.ExamEvent
import com.example.victvsschedule.ui.components.FilterDrawerSheetContent
import com.example.victvsschedule.ui.views.ListView
import com.example.victvsschedule.ui.views.MapView
import com.example.victvsschedule.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

// modal navigation drawer layout with scaffold for main screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    viewModel: BaseViewModel
) {
    // set variables from current ui state data
    val onEvent = viewModel::onEvent
    val collectedUiState: State<UiState> = viewModel.uiState.collectAsState()
    val onListView = collectedUiState.value.onListView
    val filteredExams = collectedUiState.value.filteredExams
    val filterApplied = collectedUiState.value.filterApplied

    // nav drawer and scope
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // declaring drawer layout
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // icon button to close drawer
                    IconButton(onClick = {
                        scope.launch { drawerState.close() }
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "close")
                    }
                }

                //// main content defined in separate composable to aid readability and scalability
                FilterDrawerSheetContent(viewModel = viewModel)
                ///////////////////////////////////////////////////////////////////////////////////

                // box containing button which is conditional depending on filter results
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = 12.dp)
                ) {
                    Column {
                        if (filterApplied && filteredExams.isNotEmpty()) {
                            Button(
                                onClick = { scope.launch { drawerState.close() } }
                            ) {
                                Text(text = "${filteredExams.size} results")
                            }
                        } else if (filterApplied) {
                            Button(
                                enabled = false,
                                onClick = {}
                            ) {
                                Text(text = "0 results")
                            }
                        } else {
                            Button(
                                onClick = { scope.launch { drawerState.close() } }
                            )
                            {
                                Text(text = "Cancel")
                            }
                        }
                    }
                }
            }
        },
        // stop drawer from being swiped open/closed
        gesturesEnabled = false
    ) {
        // scaffold for top app bar
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    title = {
                        Text(text = "Exams")
                    },
                    // if in list view tab, display filter icon button
                    actions = {
                        if (onListView) {
                            IconButton(
                                // open drawer on click
                                onClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }) {
                                Icon(Icons.Default.FilterList, contentDescription = "filter")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->

            // set ab state and tab options
            var tabState = collectedUiState.value.tabIndex
            val titles = listOf("List view", "Map view")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                // generate tab for each option in list
                TabRow(selectedTabIndex = tabState) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(text = title) },
                            selected = tabState == index,
                            // set tab state in ui state and within composable
                            onClick = {
                                onEvent(ExamEvent.SelectTab(tabState))
                                tabState = index
                            }
                        )
                    }
                }
                // load view composable depending on tab selection
                if (tabState == 0) {
                    ListView(
                        uiState = viewModel.uiState,
                        onEvent = onEvent
                    )
                } else {
                    MapView(
                        uiState = viewModel.uiState,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}