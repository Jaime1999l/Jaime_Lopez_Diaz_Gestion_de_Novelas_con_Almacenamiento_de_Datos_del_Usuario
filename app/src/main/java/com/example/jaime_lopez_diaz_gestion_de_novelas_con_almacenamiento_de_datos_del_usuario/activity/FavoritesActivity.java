package com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.R;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.domain.Novel;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.databaseSQL.SQLiteHelper;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.ui.favoritesNovel.FavoritesViewModel;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private FavoritesViewModel favoritesViewModel;
    private LinearLayout favoritesLayout;
    private SQLiteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_activity);

        favoritesLayout = findViewById(R.id.favorites_layout);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        sqliteHelper = new SQLiteHelper(this);

        // Cargar novelas favoritas desde SQLite y Firebase
        loadFavoriteNovelsFromSQLite();
        loadFavoriteNovelsFromFirebase();
    }

    // Método para cargar novelas favoritas desde SQLite
    private void loadFavoriteNovelsFromSQLite() {
        List<Novel> favoriteNovelsFromSQLite = sqliteHelper.getFavoriteNovels();
        displayFavoriteNovels(favoriteNovelsFromSQLite);
    }

    // Método para cargar novelas favoritas desde Firebase
    private void loadFavoriteNovelsFromFirebase() {
        favoritesViewModel.getFavoriteNovels().observe(this, novels -> {
            for (Novel novel : novels) {
                sqliteHelper.addNovel(novel);
            }
            displayFavoriteNovels(novels);
        });
    }

    private void displayFavoriteNovels(List<Novel> novels) {
        favoritesLayout.removeAllViews();

        if (novels == null || novels.isEmpty()) {
            TextView noFavoritesTextView = new TextView(this);
            noFavoritesTextView.setText("No hay novelas favoritas.");
            favoritesLayout.addView(noFavoritesTextView);
            return;
        }

        for (Novel novel : novels) {
            TextView novelView = new TextView(this);
            novelView.setText(novel.getTitle() + "\n" + novel.getAuthor());
            novelView.setPadding(16, 16, 16, 16);
            favoritesLayout.addView(novelView);
        }
    }
}
