package com.example.safespace.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.safespace.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(){
    private lateinit var myBottomNav: BottomNavigationView
    private lateinit var accountFragment: AccountFragment
    private lateinit var livechatFragment: LivechatFragment
    private lateinit var articleFragment: ArticleFragment
    private lateinit var reportFragment: ReportFragment
    private lateinit var sosFragment: SosFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        hideStatusBar()

        verifyUserAccess()





        // Setup bottom navigation bar
        myBottomNav = findViewById(R.id.bottom_nav)


        // Load Article(Home Activity) after user login
        articleFragment = ArticleFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, articleFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        // When navigation items are clicked on
        myBottomNav.setOnNavigationItemSelectedListener {item ->
            when(item.itemId){
                R.id.action_account ->{
                    accountFragment = AccountFragment()
                    Log.d("AccountFragment", "clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_host, accountFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                }
                R.id.action_livechat ->{
                    livechatFragment = LivechatFragment()
                    Log.d("LivechatFragment", "clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_host, livechatFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                }
                R.id.action_home ->{
                    articleFragment =
                        ArticleFragment()
                    Log.d("HomeFragment","Clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_host, articleFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                }
                R.id.action_SOS ->{
                    sosFragment = SosFragment()
                    Log.d("SosFragment","Clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_host, sosFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                }
                R.id.action_report ->{
                    reportFragment = ReportFragment()
                    Log.d("ReportFragment","Clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_host, reportFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                }


            }
            true

        }





    }

    private fun verifyUserAccess(){
        // If user has not logged in, take user to login page
        val user = FirebaseAuth.getInstance().currentUser
        if(user==null){
            val intent = Intent(this, LoginActivity::class.java)
            // Clear stack
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun hideStatusBar(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_home)
    }
}