package br.edu.infnet.anotations.ui.main

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.infnet.anotations.R
import br.edu.infnet.anotations.domain.adapter.GetAllAnotationsAdapter
import br.edu.infnet.anotations.domain.exception.AnotationError
import br.edu.infnet.anotations.domain.models.Anotation
import br.edu.infnet.anotations.ui.create.CreateActivity
import br.edu.infnet.anotations.ui.login.LoginActivity
import br.edu.infnet.anotations.util.Crypto
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private var userId = ""
    private var drawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewMain)
        layoutManager = LinearLayoutManager(this)
        drawer = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        for (profile in user?.providerData!!) {
            userId = profile.uid
        }

        setNavigationView()
        setAdapter()


        floating.setOnClickListener {
            val intent = Intent(applicationContext, CreateActivity::class.java)
            startActivity(intent)
        }

        setAd()
    }

    private fun retrieveList(lista: String): Anotation {
        var removeSuffix = lista.removeSuffix(".fig")
        removeSuffix = removeSuffix.removeSuffix(".txt")
        val imagem = Crypto.cryptoReadImage("$removeSuffix.fig", this)
        val description = Crypto.cryptoReadText("$removeSuffix.txt", this)[0]
        val title = lista.split("*")[0]
        val date = lista.split("*")[1].removeSuffix("*")
        val bmp = BitmapFactory.decodeByteArray(imagem, 0, imagem.size)

        return Anotation(title, description, date, bmp)
    }

    private fun setAdapter() {
        val data = ArrayList<Anotation>()
        val folder = applicationContext.filesDir
        val nFolder = File(folder, "archives")
        val path = File(nFolder.toURI())
        var prefix = ""
        val files = path.listFiles()
        try {
            files?.forEach {
                prefix = it.name.removeSuffix(".txt")
                prefix = it.name.removeSuffix(".fig")
                if (it.nameWithoutExtension != prefix) {
                    data.add(retrieveList(prefix))
                }
                recyclerView.adapter = GetAllAnotationsAdapter(data)
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)

            }
        } catch (e: AnotationError) {
            makeText("Erro ao retornar os dados.")
            throw AnotationError("Erro ao retornar os dados.", e)

        }

    }

    private fun setAd() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer?.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun setNavigationView() {
        val user = auth.currentUser

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        val navigationView = findViewById<NavigationView>(R.id.navigation)

        for (profile in user?.providerData!!) {
            val name = profile.displayName
            val email = profile.email

            val nName = navigationView.getHeaderView(0).findViewById<TextView>(R.id.txtName)
            val nEmail = navigationView.getHeaderView(0).findViewById<TextView>(R.id.txtEmail)

            nName.text = name
            nEmail.text = email
        }

        val toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer?.addDrawerListener(toggle)
        drawer?.isClickable = true
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        navigationView.setCheckedItem(R.id.home)

        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer?.closeDrawers()

            when (it.itemId) {
                R.id.home -> {
                    return@setNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    logout()
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun makeText(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }
}