package com.gallon.actionrecord

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.example.android.navigationdrawer.DrawerAdapter
import com.gallon.actionrecord.model.Action
import com.gallon.actionrecord.model.ActionUnit
import com.gallon.actionrecord.service.ReplayService
import com.gallon.actionrecord.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var mTitle: CharSequence
    private lateinit var mDrawerTitle: CharSequence
    private val planetTitles = ArrayList<String>()
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var active = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService(Intent(Utils.getApp(), ReplayService::class.java))

        mTitle = title
        mDrawerTitle = title

        initListener()

        // set a custom shadow that overlays the main content when the drawer opens
        drawer_layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
        // improve performance by indicating the list if fixed size.
        left_drawer.setHasFixedSize(true)

        // set up the drawer's list view with items and click listener
        left_drawer.adapter = DrawerAdapter(planetTitles, object : DrawerAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                selectItem(position)
            }
        })
        // enable ActionBar app icon to behave as action to toggle nav drawer
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = object : ActionBarDrawerToggle(
                this, /* host Activity */
                drawer_layout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            override fun onDrawerClosed(view: View) {
                supportActionBar!!.title = mTitle
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportActionBar!!.title = mDrawerTitle
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }
        drawer_layout.setDrawerListener(drawerToggle)
    }

    private var state = ACTION_IDLE

    private val actionList = ArrayList<Action>()

    private fun initListener() {
        bt_replay.setOnClickListener { //回放最后
            Log.e(TAG, "actionList : $actionList")
            if (actionList.isEmpty()) {
                ToastUtils.showShort("还没有录制动作")
            } else {
                replay_view.clearDraw()
                Thread {
                    run {
                        replay_view.setEnable(true)
                        ActionHelper.play(actionList.last())
                        replay_view.setEnable(false)
                    }
                }.start()
            }
        }
        bt_record.setOnClickListener { //录制
            state = ACTION_RECORD
            ll_container.visibility = View.GONE
            replay_view.setEnable(true)
            replay_view.clearDraw()
        }
        val unitList = ArrayList<ActionUnit>()
        replay_view.setOnTouchListener { v, event ->
            if (state == ACTION_IDLE) return@setOnTouchListener false
            replay_view.refreshView(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    println("down")
                    val unit = ActionUnit().apply {
                        action = event.action
                        actionTime = System.currentTimeMillis()
                        rawX = event.rawX
                        rawY = event.rawY
                    }
                    unitList.add(unit)
                }
                MotionEvent.ACTION_MOVE -> {
                    println("move")
                    val unit = ActionUnit().apply {
                        action = event.action
                        actionTime = System.currentTimeMillis()
                        rawX = event.rawX
                        rawY = event.rawY
                    }
                    unitList.add(unit)
                }
                MotionEvent.ACTION_UP -> {
                    println("up")
                    val unit = ActionUnit().apply {
                        action = event.action
                        actionTime = System.currentTimeMillis()
                        rawX = event.rawX
                        rawY = event.rawY
                    }
                    unitList.add(unit)
                    if (unitList.count { it.action == MotionEvent.ACTION_DOWN } != 1) {
                        ToastUtils.showShort("该动作录制失败，请重试")
                    } else {
                        ToastUtils.showShort("该动作录制成功")
                        actionList.add(Action(ACTION_TYPE_SWIPE, unitList.clone() as MutableList<ActionUnit>, null))
                        planetTitles.add("动作${actionList.size}: tap/swipe")
                        left_drawer.adapter.notifyItemInserted(planetTitles.size)
                        state = ACTION_IDLE
                        replay_view.clearDraw()
                        ll_container.visibility = View.VISIBLE
                        replay_view.setEnable(false)
                        unitList.clear()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    state = ACTION_IDLE
                    ll_container.visibility = View.VISIBLE
                    replay_view.setEnable(false)
                    unitList.clear()
                }
            }
            return@setOnTouchListener true
        }
        bt_insert_delay.setOnClickListener {
            ToastUtils.showShort("该动作录制成功")
            actionList.add(Action(ACTION_TYPE_DELAY, null, et_insert_delay.text.toString().toLong()))
        }
        switch_test.setOnCheckedChangeListener { compoundButton, b ->
            active = b
        }
    }

    override fun setTitle(title: CharSequence) {
        mTitle = title
        supportActionBar!!.title = mTitle
    }

    private fun selectItem(position: Int) {
        Log.d(TAG, "selectItem: $position")
        fragmentManager.beginTransaction().run {
//            replace(R.id.content_frame, PlanetFragment.newInstance(position))
//            commit()
        }
        title = planetTitles[position]
        drawer_layout.closeDrawer(left_drawer)

        replay_view.clearDraw()
        if (active) {
            handler.postDelayed({
                replay_view.setEnable(true)
                startService(Intent(Utils.getApp(), ReplayService::class.java).apply {
                    putExtra("actionList", actionList)
                })
            }, 2000)
        } else {
            Thread {
                Thread.sleep(1000)
                replay_view.setEnable(true)
                ActionHelper.play(actionList[position])
                replay_view.setEnable(false)
            }.start()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.navigation_drawer, menu)
//        return true
//    }

    /**
     * Called whenever we call [invalidateOptionsMenu].
     * If the nav drawer is open, hide action items related to the content view.
     */
//    override fun onPrepareOptionsMenu(menu: Menu) =
//            super.onPrepareOptionsMenu(menu.apply {
//                findItem(R.id.action_websearch).isVisible = !drawerLayout.isDrawerOpen(drawerList)
//            })

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        // Handle action buttons
        return when (item.itemId) {
            0 -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * If [ActionBarDrawerToggle] is used, it must be called in [onPostCreate] and
     * [onConfigurationChanged].
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggle.
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        startService(Intent(Utils.getApp(), ReplayService::class.java).apply {
            putExtra("type", FINISH)
        })
    }

}
