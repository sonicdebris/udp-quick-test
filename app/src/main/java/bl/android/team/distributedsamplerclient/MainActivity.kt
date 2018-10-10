package bl.android.team.distributedsamplerclient

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.math.pow


class MainActivity : AppCompatActivity(), SensorEventListener {

    val clientSocket = DatagramSocket()
    val serverSocket = DatagramSocket()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.the_button).setOnClickListener {
            sendUdp()
        }

        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME)

        //receiveUdp()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        clientSocket.close()
//    }

    fun sendUdp() {
        val address = InetAddress.getByName("192.168.1.151")
        //val address = InetAddress.getByName("192.168.1.47")
        //val address = InetAddress.getByName("10.42.0.1")
        val sendData = "a".toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, address, 9876)
        clientSocket.send(sendPacket)
    }

    fun receiveUdp() {
        thread {
            val receiveData = ByteArray(1024)
            while (true) {
                val receivePacket = DatagramPacket(receiveData, receiveData.size)
                serverSocket.receive(receivePacket)
                receivePacket.length
                val sentence = String(receivePacket.data.copyOf(receivePacket.length))
                Log.i("UDP","RECEIVED: " + sentence)

                //val IPAddress = receivePacket.address
                //val port = receivePacket.port
                //val capitalizedSentence = sentence.toUpperCase()
                //sendData = capitalizedSentence.toByteArray()
                //val sendPacket = DatagramPacket(sendData, sendData.size, IPAddress, port)
                //serverSocket.send(sendPacket)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    var max = 0f
    var last = -1f

    override fun onSensorChanged(ev: SensorEvent?) {
        ev ?: return
        val mod = ev.values.take(3).map { it * it }.sum().pow(0.5f)
        if (last != -1f) {
            val delta = mod - last
            if (delta.absoluteValue > 1) {
                Log.i("YO", "ACCEL: delta $delta, values ${ev.values.size}")
            }
            if (delta > max) {
                max = delta
                findViewById<Button>(R.id.the_button).setText("delta: $max")
            }
        }
        last = mod
    }

}
