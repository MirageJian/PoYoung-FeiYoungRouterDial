package com.crackfeiyoung

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.app.AlertDialog
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.telephony.TelephonyManager
import android.widget.EditText
import com.server_auth.AuthServer

import com.server_auth.HttpClientHelper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap

class AuthActivity : AppCompatActivity() {

    private var mProgress: View? = null//处理动画
    private var mStart: Button? = null//开始
    private var mView: View? = null
    private var mImei: String? = null
    private var mAuthButon: Button? = null
    private var mPreferences: SharedPreferences? = null
    private var mUsername: String? = null
    private var mImei_text: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // change the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        // control the android sdk > 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_auth)

        mView = findViewById(R.id.auth_view)
        mProgress = findViewById(R.id.pro_guide)
        mImei_text = findViewById(R.id.imei_text)
        // 按钮初始化，设置监听
        mStart = findViewById(R.id.btn_start)
        mStart!!.setOnClickListener {
            val intent = Intent(this@AuthActivity, LoginActivity::class.java)
            intent.putExtra("username", mUsername)
            this@AuthActivity.startActivity(intent)
        }
        mAuthButon = findViewById(R.id.btn_auth)
        mAuthButon!!.setOnClickListener {
            showProcessBar(true)
            checkUpgrade()
        }
        //设置参数初始化
        mPreferences = getPreferences(Context.MODE_PRIVATE)
        if (mPreferences!!.getString("new_app", "") == "true") {
            val intent = Intent(this@AuthActivity, LoginActivity::class.java)
            this@AuthActivity.startActivity(intent)
        }
    }

    private fun islacksOfPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_DENIED
        } else false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0x12) {
            val tel = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                mImei = tel.deviceId

            } catch (ex: SecurityException) {
                ex.printStackTrace()
                mImei = "null"
            }

            mImei_text!!.setText(mImei)
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (islacksOfPermission(PERMISSION[0])) {
            ActivityCompat.requestPermissions(this, PERMISSION, 0x12)
        } else {
            val tel = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                mImei = tel.deviceId

            } catch (ex: SecurityException) {
                ex.printStackTrace()
                mImei = "null"
            }

            mImei_text!!.setText(mImei)
        }
    }

    // 验证的背景方法
    private inner class AuthAsync : AsyncTask<Void, Void, String>() {

        fun sendGet(urlIn: String): String {
            var result = ""
            var inData: BufferedReader? = null//获取数据的变量
            try {
                val url = URL(urlIn)//设置url
                val connection = url.openConnection() as HttpURLConnection//打开链接
                connection.connect()
                //取得响应头
                val map = connection.headerFields
                //设置读取文件的编码格式和读取文件
                inData = BufferedReader(InputStreamReader(
                        connection.inputStream, "UTF-8")
                )
                var line: String? = inData.readLine()//读取内容
                while (line != null) {
                    result += line
                    line = inData.readLine()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            } finally {
                try {
                    if (inData != null) {
                        inData.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return result
        }

        override fun doInBackground(vararg params: Void): String? {
            val result: String?
            val param = "?imei=" + mImei!!
            try {
                println(param)
                result = sendGet(AuthServer.AUTH_HOST + param)
                return result
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(success: String?) {
            if (success == null) {
                showProcessBar(false)
                showStart(false)
                Snackbar.make(mView!!, "服务器连接失败", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            } else if (success.isEmpty()) {
                showProcessBar(false)
                showStart(false)
                Snackbar.make(mView!!, "你的设备没有被授权", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                // 设置启动参数
                val editor = mPreferences!!.edit()
                editor.putString("new_app", "false")
                editor.apply()
            } else {
                showProcessBar(false)
                showStart(true)
                // 设置启动参数
                mUsername = success
                val editor = mPreferences!!.edit()
                editor.putString("new_app", "true")
                editor.apply()
            }
        }

        // 若执行了取消方法，直到整个线程跑完才执行onCancelled
        override fun onCancelled() {}
    }

    private fun showStart(show: Boolean) {
        mStart!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showProcessBar(show: Boolean) {
        mProgress!!.visibility = if (show) View.VISIBLE else View.GONE
        mAuthButon!!.isEnabled = !show
    }

    companion object {
        // 下面三个方式是用来动态获取系统权限的
        private val PERMISSION = arrayOf(Manifest.permission.READ_PHONE_STATE)
    }

    // check updating
    private fun checkUpgrade() {
        val params = HashMap<String, String>()
        params["imei"] = "apk"
        HttpClientHelper().setUrl(AuthServer.AUTH_HOST)
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setSuccessCallback { result ->
                    if (result != "/PoYoung" + AuthServer.version + ".apk") {
                        showProcessBar(false)
                        showStart(false)
                        // 设置启动参数
                        val editor = mPreferences!!.edit()
                        editor.putString("new_app", "false")
                        editor.apply()
                        AuthServer.apkName = result
                        AlertDialog.Builder(this@AuthActivity).setTitle("新版本")
                                .setMessage("破样发布了新的版本，请更新")
                                .setCancelable(false)
                                .setPositiveButton("好") { dialogInterface, i ->
                                    val uri = Uri.parse(AuthServer.FILE_HOST + AuthServer.apkName)
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(intent)
                                 }
                                .setNegativeButton("偏不更新") { dialogInterface, i -> }.create().show()
                    } else {
                        AuthAsync().execute()
                    }
                }
                .setErrorCallback {
                    showProcessBar(false)
                    showStart(false)
                    // 设置启动参数
                    val editor = mPreferences!!.edit()
                    editor.putString("new_app", "false")
                    editor.apply()
                    AlertDialog.Builder(this@AuthActivity).setTitle("出错")
                            .setMessage("链接验证服务器失败")//设置显示的内容
                            .setCancelable(false)
                            .setPositiveButton("好") { dialogInterface, i -> }
                            .create().show()
                }.doTask()
    }
}
