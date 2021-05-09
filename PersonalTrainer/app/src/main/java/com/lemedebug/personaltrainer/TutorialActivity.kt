package com.lemedebug.personaltrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.lemedebug.personaltrainer.exercise.TutorialsAdapter
import com.lemedebug.personaltrainer.models.TutorialItem

class TutorialActivity : AppCompatActivity() {

    private lateinit var tutorialAdapter:TutorialsAdapter
    private lateinit var layoutTutorialIndicators:LinearLayout
    private lateinit var buttonTutorialAction:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        layoutTutorialIndicators = findViewById(R.id.layoutTutorialIndicators)
        buttonTutorialAction = findViewById(R.id.buttonTutorialAction)

        setupTutorialItems()

        val tutorialViewPager:ViewPager2 = findViewById(R.id.tutorialViewPager)
        tutorialViewPager.adapter = tutorialAdapter

        setupTutorialIndicators()
        setCurrentTutorialIndicators(0)

        tutorialViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentTutorialIndicators(position)
            }
        })

        buttonTutorialAction.setOnClickListener {
            if (tutorialViewPager.currentItem + 1 < tutorialAdapter.itemCount){
                tutorialViewPager.currentItem = tutorialViewPager.currentItem+1
            }else{
                startActivity(Intent(applicationContext,WorkoutActivity::class.java))
                finish()
            }
        }
    }

    private fun setupTutorialItems(){

        val tutorialItems:ArrayList<TutorialItem> = ArrayList()

        val itemOne:TutorialItem = TutorialItem(
            R.drawable.img_create_workout,
            "Create Personalized Workout",
            "Create your own personalized workouts with desired exercises and repetitions."
        )

        val itemTwo:TutorialItem = TutorialItem(
            R.drawable.img_edit_workout,
            "Edit Workout",
            "Read details about hundreds of exercises and add desired suitable for you with defined repetitions. All workouts are saved and editable"
        )


        val itemThree:TutorialItem = TutorialItem(
            R.drawable.img_play_workout,
            "Play Workout",
            "Play personalized self created workout with desired reps. Later on completed workouts can be checked in History."
        )

        tutorialItems.add(itemOne)
        tutorialItems.add(itemTwo)
        tutorialItems.add(itemThree)

        tutorialAdapter = TutorialsAdapter(tutorialItems)
    }

    private fun setupTutorialIndicators(){
        val indicators =  arrayOfNulls<ImageView>(tutorialAdapter.itemCount)
        val layoutParams:LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8,0,8,0)
        for (i in 0 until tutorialAdapter.itemCount){
            indicators[i] = ImageView(applicationContext)
            indicators[i]!!.setImageDrawable(ContextCompat.getDrawable(
                applicationContext,
                R.drawable.tutuorial_indicator_inactive
            ))
            indicators[i]!!.layoutParams = layoutParams
            layoutTutorialIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentTutorialIndicators(index:Int){
        val childCount:Int = layoutTutorialIndicators.childCount
        for(i in 0 until childCount){
            val imageView:ImageView = layoutTutorialIndicators.getChildAt(i) as ImageView
            if (i == index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext,R.drawable.tutuorial_indicator_active)
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext,R.drawable.tutuorial_indicator_inactive)
                )
            }
        }
        if (index == tutorialAdapter.itemCount - 1){
            buttonTutorialAction.text = "START"
        }else{
            buttonTutorialAction.text = "NEXT"
        }
    }
}