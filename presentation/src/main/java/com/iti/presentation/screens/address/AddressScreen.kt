@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onAddressSelected: ((Address) -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val localSnackbarHostState = remember { SnackbarHostState() }
    val effectiveSnackbarHostState = snackbarHostState ?: localSnackbarHostState

    val screenState = state.screenState
    val successMessage = stringResource(R.string.address_success_added)

    LaunchedEffect(state.showSuccessBadge) {
        if (state.showSuccessBadge) {
            effectiveSnackbarHostState.showSuccess(successMessage)
            viewModel.sendIntent(AddressContract.Intent.DismissSuccessBadge)
        }
    }

    LaunchedEffect(state.errorText) {
        val message = state.errorText?.resolve(context)
        if (!message.isNullOrEmpty()) {
            effectiveSnackbarHostState.showError(message)
            viewModel.sendIntent(AddressContract.Intent.ClearError)
        }
    }

    LaunchedEffect(true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddressContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is AddressContract.Effect.ShowMessage -> {
                    val messageString = effect.message.resolve(context)
                    effectiveSnackbarHostState.showError(messageString)
                }
                else -> Unit
            }
        }
    }

    if (screenState !is AddressContract.ScreenState.MapPicker) {
        LocationPermissionHandler(
            onPermissionGranted = {
                viewModel.sendIntent(AddressContract.Intent.PermissionGranted)
            },
            onPermissionDenied = {
                viewModel.sendIntent(AddressContract.Intent.PermissionDenied)
            },
            triggerRequest = state.triggerPermissionRequest,
        )
    }

    val topBarTitle = when (screenState) {
        is AddressContract.ScreenState.LocationDetected -> {
            if (screenState.isFromGps) {
                stringResource(R.string.address_location_detected_title)
            } else {
                stringResource(R.string.address_heading_selected)
            }
        }
        else -> stringResource(R.string.address_title)
    }

    val topBarNavigationAction = {
        when (screenState) {
            is AddressContract.ScreenState.LocationDetected -> {
                viewModel.sendIntent(AddressContract.Intent.CancelAddAddress)
            }
            else -> {
                viewModel.sendIntent(AddressContract.Intent.NavigateBack)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            if (onAddressSelected == null &&
                screenState !is AddressContract.ScreenState.MapPicker &&
                screenState !is AddressContract.ScreenState.LocationDetected) {
                BackTopBar(
                    title = topBarTitle,
                    onBack = topBarNavigationAction,
                    actions = {
                        val showAddIcon = when (screenState) {
                            AddressContract.ScreenState.Empty,
                            is AddressContract.ScreenState.Success -> true
                            else -> false
                        }
                        if (showAddIcon) {
                            IconButton(
                                onClick = {
                                    viewModel.sendIntent(AddressContract.Intent.AddAddressClicked)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.address_action_add),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        val contentPadding =
            if (screenState is AddressContract.ScreenState.MapPicker ||
                screenState is AddressContract.ScreenState.LocationDetected
            ) {
                PaddingValues(0.dp)
            } else {
                innerPadding
            }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {

            AnimatedContent(
                targetState = screenState,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "ScreenStateTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetState ->

                when (targetState) {

                    AddressContract.ScreenState.Loading -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    AddressContract.ScreenState.Empty -> {
                        AddressEmptyState {
                            viewModel.sendIntent(AddressContract.Intent.AddAddressClicked)
                        }
                    }

                    is AddressContract.ScreenState.LocationDetected -> {
                        AddressLocationDetected(
                            address = targetState.address,
                            isFromGps = targetState.isFromGps,
                            onConfirmClick = { tagName, isDefault ->
                                viewModel.sendIntent(
                                    AddressContract.Intent.ConfirmAddress(tagName, isDefault)
                                )
                            },
                            onEditLocationClick = {
                                viewModel.sendIntent(AddressContract.Intent.OpenMapPicker)
                            }
                        )
                    }

                    is AddressContract.ScreenState.MapPicker -> {
                        AddressMapPicker(
                            initialLatitude = targetState.initialLatitude,
                            initialLongitude = targetState.initialLongitude,
                            onLocationConfirmed = { lat, lng ->
                                viewModel.sendIntent(
                                    AddressContract.Intent.LocationSelectedFromMap(lat, lng)
                                )
                            },
                            onBackClick = {
                                viewModel.sendIntent(AddressContract.Intent.CancelMapPicker)
                            },
                            viewModel = viewModel
                        )
                    }

                    is AddressContract.ScreenState.Success -> {
                        AddressListView(
                            addresses = targetState.addresses,
                            onDeleteAddress = {
                                viewModel.sendIntent(AddressContract.Intent.DeleteAddress(it))
                            },
                            onSetDefaultAddress = {
                                viewModel.sendIntent(AddressContract.Intent.SetDefaultAddress(it))
                            },
                            onAddressSelected = onAddressSelected
                        )
                    }

                    is AddressContract.ScreenState.Failure -> {
                        ErrorScreen(
                            message = targetState.message,
                            onRetry = {
                                viewModel.sendIntent(AddressContract.Intent.LoadAddresses)
                            }
                        )
                    }
                }
            }

            if (snackbarHostState == null) {
                ShopIQSnackBarHost(
                    hostState = effectiveSnackbarHostState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}