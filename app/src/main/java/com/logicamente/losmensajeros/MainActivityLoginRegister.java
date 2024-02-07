package com.logicamente.losmensajeros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivityLoginRegister extends AppCompatActivity {
    Button btnsoyusuario, btnregistrame;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (sesionIniciada()) {
            // Redirigir al usuario a la actividad principal
            iniciarActividadPrincipal();
            return;
        }


        setContentView(R.layout.activity_mainloginregister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnsoyusuario = findViewById(R.id.btnLogin);
        btnregistrame = findViewById(R.id.btnRegistrarme);


        // Establecer OnClickListener para los botones
        btnsoyusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre de usuario y la contraseña ingresados por el usuario
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Ejecutar AsyncTask para iniciar sesión
                new IniciarSesionTask().execute(username, password);
            }
        });

        btnregistrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones cuando se hace clic en el botón "Regístrame"
                seleccionarregistrarme();
            }
        });
    }

    private boolean sesionIniciada() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("sesionIniciada", false);
    }

    // Método para iniciar la actividad principal
    private void iniciarActividadPrincipal() {
        Intent intent = new Intent(this, MainActivityUsuario.class);
        startActivity(intent);
        finish(); // Finalizar la actividad de inicio de sesión para evitar que el usuario pueda volver atrás
    }




    private void seleccionarregistrarme() {
        Intent intent = new Intent(this,MainActivityRegister.class);
        startActivity(intent);
    }

    // Clase AsyncTask para manejar el inicio de sesión en segundo plano
    private class IniciarSesionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlServidor = "http://logicamente.com.ar/login.php";
            String usuario = params[0];
            String pass = params[1];

            try {
                String parametros = "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                        "&pass=" + URLEncoder.encode(pass, "UTF-8");
                URL url = new URL(urlServidor + "?" + parametros);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = bufferedReader.readLine()) != null) {
                    respuesta.append(linea);
                }
                bufferedReader.close();
                conexion.disconnect();

                return respuesta.toString();


            } catch (IOException e) {
                Log.e("IniciarSesionTask", "Error al enviar la solicitud HTTP", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String resultado) {

            // Dentro de onPostExecute()
            if (resultado != null && resultado.equals("success")) {
                // Guardar el indicador de sesión iniciada en SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivityLoginRegister.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("sesionIniciada", true);
                editor.apply();

                // Autenticación exitosa, iniciar la actividad principal
                iniciarActividadPrincipal();
            }

        }



    }


}
