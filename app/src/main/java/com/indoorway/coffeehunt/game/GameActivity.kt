package com.indoorway.coffeehunt.game

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import android.widget.Toast
import com.indoorway.coffeehunt.R
import com.indoorway.coffeehunt.common.DI.provideLoginRepository
import com.indoorway.coffeehunt.common.enterImmersiveFullScreenMode
import com.indoorway.coffeehunt.game.ar.toPositionOnScreen
import com.indoorway.coffeehunt.game.camera.CameraController
import com.indoorway.coffeehunt.game.camera.LiveStream
import com.indoorway.coffeehunt.game.core.Game
import com.indoorway.coffeehunt.game.core.items
import com.indoorway.coffeehunt.game.logout.LogoutController
import com.indoorway.coffeehunt.game.logout.LogoutView
import com.indoorway.coffeehunt.game.minimap.GameMapView
import com.indoorway.coffeehunt.game.score.ScoreController
import com.indoorway.coffeehunt.game.score.ScoreRepositoryImpl
import com.indoorway.coffeehunt.game.score.ScoreView
import com.indoorway.coffeehunt.game.score.toScoreText
import com.indoorway.coffeehunt.game.sensors.RxIndoorway
import com.indoorway.coffeehunt.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.current_score_layout.*
import kotlinx.android.synthetic.main.game_activity.*
import kotlinx.android.synthetic.main.score_view.*

class GameActivity : RxActivity(), LiveStream.View, LogoutView {

    private val gameMapView by lazy { GameMapView(miniMapView, RxIndoorway.mapConfig) }
    private val phases = DI.phases
    private val states = DI.states
    private val cameraController = CameraController(this)
    private val scrollView: ScoreView by lazy { ScoreView(scoreViewContainer, licencesView) }
    private val scoreController by lazy {
        ScoreController(
                view = scrollView,
                repository = ScoreRepositoryImpl(),
                startNewGame = { phases.onNext(Game.Phase.STARTED) })
    }
    private val logoutController by lazy { LogoutController(this, provideLoginRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        startGame.setOnClickListener { scoreController.onStartNewGame() }
        licencesButton.setOnClickListener { scoreController.onLicences() }
        logoutButton.setOnClickListener { showLogoutDialog() }
    }

    override fun onResume() {
        super.onResume()
        val cameraWrapper = DI.provideNewCameraWrapper()
        startCameraLiveStream(cameraWrapper)
        phases.onNext(Game.Phase.STARTED)
        subscribeToItemsUpdates(cameraWrapper)
        subscribeToScoreUpdates()
        subscribeToPlayerDeaths()
        subscribeToNewStates()
        subscribeMinimapToHeadingUpdates()
    }

    private fun subscribeToNewStates() {
        states
                .takeUntil(Lifecycle.PAUSE)
                .subscribe(gameMapView::display)
    }

    private fun subscribeToPlayerDeaths() {
        states
                .filter { it.player is Game.Player.Existent.Dead }
                .map { it.player.score }
                .takeUntil(Lifecycle.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scoreController::showGameFinalScore)
    }

    private fun subscribeToScoreUpdates() {
        states
                .map { it.player.score.points }
                .distinctUntilChanged()
                .takeUntil(Lifecycle.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { currentScoreView.text = it.toScoreText() }
    }

    private fun subscribeToItemsUpdates(cameraWrapper: LiveStream.CameraWrapper) {
        states
                .map { it.items as Iterable<Game.Item> }
                .toPositionOnScreen(DI.pitchEvents, DI.provideHeadingObservableInRadians(), states.map { it.board.center }, cameraWrapper.getFieldOfView(), DI.provideUserPositions())
                .takeUntil(Lifecycle.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { arView.showItems(it) }
    }

    private fun subscribeMinimapToHeadingUpdates() {
        DI.headings
                .takeUntil(Lifecycle.PAUSE)
                .subscribe { miniMapView.cameraControl.setMapRotation(it) }
    }

    override fun onPause() {
        phases.onNext(Game.Phase.FINISHED)
        stopCameraLiveStream()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) = enterImmersiveFullScreenMode(hasFocus)

    override fun onBackPressed() {
        scrollView.onBackPressed { super.onBackPressed() }
    }

    override fun showLiveStreamFromCamera(liveStream: View) {
        liveStreamContainer.addView(liveStream)
    }

    override fun showCameraError() {
        Toast.makeText(this, R.string.camera_error, Toast.LENGTH_LONG).show()
    }

    override fun openLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun startCameraLiveStream(cameraWrapper: LiveStream.CameraWrapper) {
        cameraController.startLiveStream(cameraWrapper)
    }

    private fun stopCameraLiveStream() {
        cameraController.stopLiveStream()
        liveStreamContainer.removeAllViews()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_title))
                .setPositiveButton(getString(R.string.yes), { _, _ -> logoutController.signOut() })
                .setNegativeButton(getString(R.string.no), null)
                .show()
    }
}
