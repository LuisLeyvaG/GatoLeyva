package com.example.gatoleyva

import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gatoleyva.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    // DataBinding
    private lateinit var binding: ActivityMainBinding

    // Nombres de jugadores
    private lateinit var player1: String
    private lateinit var player2: String

    // false: Fuego     true: Agua
    private var element: Boolean = false

    private var waterWins = 0
    private var fireWins = 0

    private lateinit var board: Array<IntArray>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initScreenGame()

    }

    private fun initScreenGame() {
        binding.rlSetPlayers.visibility = RelativeLayout.VISIBLE
        setSizeBoard()
    }

    private fun setSizeBoard() {
        var iv: ImageView

        // Obtener dimensiones de pantalla
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x

        // Conversion a dp
        var width_dp = (width / resources.displayMetrics.density)

        // Quitando margenes
        var lateralMarginsDP = 0
        val width_cell = (width_dp - lateralMarginsDP)/3
        val height_cell = width_cell


        // Establecer altura a tablero
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                iv = findViewById(resources.getIdentifier("v$i$j", "id", packageName))

                var height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height_cell, resources.displayMetrics).toInt()
                var width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width_cell, resources.displayMetrics).toInt()

                val layoutParams: ViewGroup.LayoutParams = iv.getLayoutParams()
                layoutParams.height = height
                layoutParams.width = width
                iv.setLayoutParams(layoutParams)
            }
        }


        resetBoard()
    }

    public fun pressReady(v: View) {

        var etPlayer1 = binding.etPlayer1
        var etPlayer2 = binding.etPlayer2

        var textPlayer1 = etPlayer1.text.toString()
        var textPlayer2 = etPlayer2.text.toString()

        if (textPlayer1.replace(" ","") != "" && textPlayer2.replace(" ","") != "") {
            this.player1 = textPlayer1
            this.player2 = textPlayer2

            animate(false, binding.rlSetPlayers)
            binding.rlSetPlayers.visibility = RelativeLayout.GONE

            startGame()

        } else {
            Toast.makeText(this, "Ingresa Jugadores", Toast.LENGTH_SHORT).show()
            etPlayer1.textAlignment = EditText.TEXT_ALIGNMENT_CENTER
            etPlayer2.textAlignment = EditText.TEXT_ALIGNMENT_CENTER
        }
    }

    private fun startGame() {
        binding.tvPlayer1.text = player1
        binding.tvPlayer2.text = player2

        if ((Math.random()*2).toInt() == 1) {
            binding.tvElement1.text = getString(R.string.fire_string)
            binding.tvElement2.text = getString(R.string.water_string)
        } else {
            binding.tvElement1.text = getString(R.string.water_string)
            binding.tvElement2.text = getString(R.string.fire_string)
        }

        binding.tvScore1.text = "0"
        binding.tvScore2.text = "0"

        binding.ivWinner.setImageDrawable(getDrawable(R.drawable.ic_none))
        binding.ivTurn.setImageDrawable(getDrawable(R.drawable.ic_fire))

        initBoard()

        //    Eventos de las casillas

        var iv: ImageView

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                iv = findViewById(resources.getIdentifier("v$i$j", "id", packageName))
                iv.setOnClickListener {

                    drawPosition(i, j)

                    checkWin()

                }
            }
        }

    }

    private fun animate(mostrar: Boolean, v: RelativeLayout) {
        val set = AnimationSet(true)
        var animation: Animation? = null
        animation = if (mostrar) {
            //desde la esquina inferior derecha a la superior izquierda
            TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f
            )
        } else {    //desde la esquina superior izquierda a la esquina inferior derecha
            TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0.3f,
                Animation.RELATIVE_TO_SELF,
                2.0f,
                Animation.RELATIVE_TO_SELF,
                0.3f,
                Animation.RELATIVE_TO_SELF,
                2.0f
            )
        }
        //duración en milisegundos
        animation.setDuration(1200)
        set.addAnimation(animation)
        val controller = LayoutAnimationController(set, 0.1f)
        v.setLayoutAnimation(controller)
        v.startAnimation(animation)
    }

    private fun resetBoard() {
        var iv: ImageView

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                iv = findViewById(resources.getIdentifier("v$i$j", "id", packageName))
                iv.setImageDrawable(getDrawable(R.drawable.tv_bottom_bg))
                iv.setPadding(0,0,0,0)
            }
        }
    }

    private fun initBoard() {

        // 0 casilla libre
        // 1 casilla ocupada por fuego
        // 2 casilla ocupada por agua

        board = arrayOf(
            //        00 01 02
            intArrayOf(0,0,0),
            //        10 11 12
            intArrayOf(0,0,0),
            //        20 21 22
            intArrayOf(0,0,0)
        )
    }


    ////////////////   LÓGICA DEL JUEGO   //////////////////////


    private fun drawPosition(x: Int, y: Int) {
        val valor: Int = board[x][y]

        if (valor == 0) {

            var iv: ImageView = findViewById(resources.getIdentifier("v$x$y", "id", packageName))

            if (this.element) {
                iv.setImageDrawable(getDrawable(R.drawable.ic_water))
                board[x][y] = 2
            } else {
                iv.setImageDrawable(getDrawable(R.drawable.ic_fire))
                board[x][y] = 1
            }

            var paddingDp: Int = 15;
            var density: Float = resources.displayMetrics.density
            var paddingPixel: Int = (paddingDp * density).toInt();
            iv.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);

            //  Cambiar turno
            this.element = !this.element

            if (this.element) {
                binding.ivTurn.setImageDrawable(getDrawable(R.drawable.ic_water))
            } else {
                binding.ivTurn.setImageDrawable(getDrawable(R.drawable.ic_fire))
            }
        }
    }

    private fun checkWin() {

        var elem: Int = 0
        var condicion: Boolean

        var i: Int = 0
        var j: Int = 0

        // COLUMNA 1
        elem = 1
        condicion = board[i][j] == elem && board[i][j+1] == elem && board[i][j+2] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i][j] == elem && board[i][j+1] == elem && board[i][j+2] == elem
        proveWin(elem, condicion)

        // COLUMNA 2
        elem = 1
        condicion = board[i+1][j] == elem && board[i+1][j+1] == elem && board[i+1][j+2] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i+1][j] == elem && board[i+1][j+1] == elem && board[i+1][j+2] == elem
        proveWin(elem, condicion)

        // COLUMNA 3
        elem = 1
        condicion = board[i+2][j] == elem && board[i+2][j+1] == elem && board[i+2][j+2] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i+2][j] == elem && board[i+2][j+1] == elem && board[i+2][j+2] == elem
        proveWin(elem, condicion)

        // FILA 1
        elem = 1
        condicion = board[i][j] == elem && board[i+1][j] == elem && board[i+2][j] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i][j] == elem && board[i+1][j] == elem && board[i+2][j] == elem
        proveWin(elem, condicion)

        // FILA 2
        elem = 1
        condicion = board[i][j+1] == elem && board[i+1][j+1] == elem && board[i+2][j+1] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i][j+1] == elem && board[i+1][j+1] == elem && board[i+2][j+1] == elem
        proveWin(elem, condicion)

        // FILA 3
        elem = 1
        condicion = board[i][j+2] == elem && board[i+1][j+2] == elem && board[i+2][j+2] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i][j+2] == elem && board[i+1][j+2] == elem && board[i+2][j+2] == elem
        proveWin(elem, condicion)

        // DIAGONAL TL/BR
        elem = 1
        condicion = board[i][j] == elem && board[i+1][j+1] == elem && board[i+2][j+2] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i][j] == elem && board[i+1][j+1] == elem && board[i+2][j+2] == elem
        proveWin(elem, condicion)

        // DIAGONAL TR/BL
        elem = 1
        condicion = board[i][j+2] == elem && board[i+1][j+1] == elem && board[i+2][j] == elem
        proveWin(elem, condicion)

        elem = 2
        condicion = board[i][j+2] == elem && board[i+1][j+1] == elem && board[i+2][j] == elem
        proveWin(elem, condicion)
    }

    private fun proveWin(elem: Int, condicion: Boolean) {
        if (condicion && elem == 1) {

            Toast.makeText(this, "GANO FUEGO!", Toast.LENGTH_SHORT).show()
            if (binding.tvElement1.text == getString(R.string.fire_string)) {
                this.fireWins++
                binding.tvScore1.text = this.fireWins.toString()
            } else if (binding.tvElement2.text == getString(R.string.fire_string)) {
                this.fireWins++
                binding.tvScore2.text = this.fireWins.toString()
            }

        } else if (condicion && elem == 2) {

            Toast.makeText(this, "GANO AGUA!", Toast.LENGTH_SHORT).show()
            if (binding.tvElement1.text == getString(R.string.water_string)) {
                this.waterWins++
                binding.tvScore1.text = this.waterWins.toString()
            } else if (binding.tvElement2.text == getString(R.string.water_string)) {
                this.waterWins++
                binding.tvScore2.text = this.waterWins.toString()
            }

        }

        if (this.fireWins > this.waterWins) {
            binding.ivWinner.setImageDrawable(getDrawable(R.drawable.ic_fire))
        } else if (this.waterWins > this.fireWins) {
            binding.ivWinner.setImageDrawable(getDrawable(R.drawable.ic_water))
        }
    }

    private fun checkDraw() {
        var contador: Int = 0

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[i][j] != 0) {
                    contador++
                }
            }
        }

        if (contador == 9) {
            Toast.makeText(this, "EMPATE", Toast.LENGTH_SHORT).show()
        }
    }


}