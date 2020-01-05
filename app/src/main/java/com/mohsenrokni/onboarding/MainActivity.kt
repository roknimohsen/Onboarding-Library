package com.mohsenrokni.onboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mohsenrokni.onboardinglibrary.Onboarding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val board1settings = Onboarding.BoardSetting()
        board1settings.title = "1 THIS IS THE TITLE"
        board1settings.text = "1 this is the text"
        val board1drawables = mutableListOf<Drawable>(
            resources.getDrawable(R.drawable.board_1_object_1),
            resources.getDrawable(R.drawable.board_1_object_2),
            resources.getDrawable(R.drawable.board_1_object_3),
            resources.getDrawable(R.drawable.board_1_object_4)
        )
        board1settings.drawableList = board1drawables

        val board2settings = Onboarding.BoardSetting()
        board2settings.title = "2 THIS IS THE TITLE"
        board2settings.text = "2 this is the text"
        val board2drawables = mutableListOf<Drawable>(
            resources.getDrawable(R.drawable.board_2_object_1),
            resources.getDrawable(R.drawable.board_2_object_2),
            resources.getDrawable(R.drawable.board_2_object_3)
        )
        board2settings.drawableList = board2drawables

        val board3settings = Onboarding.BoardSetting()
        board3settings.title = "3 THIS IS THE TITLE"
        board3settings.text = "3 this is the text"
        val board3drawables = mutableListOf<Drawable>(
            resources.getDrawable(R.drawable.board_3_object_1),
            resources.getDrawable(R.drawable.board_3_object_2),
            resources.getDrawable(R.drawable.board_3_object_3),
            resources.getDrawable(R.drawable.board_3_object_4)
        )
        board3settings.drawableList = board3drawables

        val board4settings = Onboarding.BoardSetting()
        board4settings.title = "4 THIS IS THE TITLE"
        board4settings.text = "4 this is the text"
        val board4drawables = mutableListOf<Drawable>(
            resources.getDrawable(R.drawable.board_4_object_1),
            resources.getDrawable(R.drawable.board_4_object_2),
            resources.getDrawable(R.drawable.board_4_object_3)
        )
        board4settings.drawableList = board4drawables

        val onboardingView = Onboarding()
            .withActivity(this)
            .withColor(R.color.onboarding_00,
                R.color.onboarding_01,
                R.color.onboarding_02,
                R.color.onboarding_03)
            .withBoardSettings(mutableListOf(board1settings, board2settings, board3settings, board4settings))
            .getView()

        main.addView(onboardingView)
    }
}
