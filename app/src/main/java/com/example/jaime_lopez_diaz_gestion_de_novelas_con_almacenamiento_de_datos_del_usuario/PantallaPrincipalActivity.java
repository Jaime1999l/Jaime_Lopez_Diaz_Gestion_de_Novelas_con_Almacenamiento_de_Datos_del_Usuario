package com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.activity.AddEditNovelActivity;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.activity.AddReviewActivity;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.activity.FavoritesActivity;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.activity.ReviewActivity;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.domain.Novel;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.databaseSQL.SQLiteHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PantallaPrincipalActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseFirestore db;
    private LinearLayout novelsLayout;
    private List<Novel> novelList;
    private ExecutorService executorService;
    private SQLiteHelper sqliteHelper;  // Nueva instancia para SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        novelsLayout = findViewById(R.id.novels_layout);
        novelList = new ArrayList<>();
        sqliteHelper = new SQLiteHelper(this);  // Inicializamos SQLiteHelper

        ImageView imageView = findViewById(R.id.home_image);
        imageView.setImageResource(R.drawable.libros);

        Button openMenuButton = findViewById(R.id.open_menu_button);
        openMenuButton.setOnClickListener(v -> openDrawer());

        setupNavigation();

        executorService = Executors.newSingleThreadExecutor();

        loadNovelsFromSQLite(); // Cargamos novelas desde SQLite primero

        // Tarea de actualización en segundo plano
        executorService.execute(this::startPolling);
    }

    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(findViewById(R.id.menu_layout));
        } else {
            Log.e("DrawerLayout", "drawerLayout is null");
        }
    }

    private void setupNavigation() {
        TextView navAddNovel = findViewById(R.id.nav_add_novel);
        navAddNovel.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipalActivity.this, AddEditNovelActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        TextView navViewFavorites = findViewById(R.id.nav_view_favorites);
        navViewFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipalActivity.this, FavoritesActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });

        TextView navViewReviews = findViewById(R.id.nav_view_reviews);
        navViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipalActivity.this, ReviewActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawers();
        });
    }

    private void loadNovelsFromSQLite() {
        // Cargar novelas desde SQLite y mostrarlas
        List<Novel> novelsFromSQLite = sqliteHelper.getAllNovels();
        if (novelsFromSQLite != null && !novelsFromSQLite.isEmpty()) {
            updateNovelList(novelsFromSQLite);
        }
    }

    private void loadNovels() {
        db.collection("novelas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Novel> newNovels = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Novel novel = document.toObject(Novel.class);
                    novel.setId(document.getId());  // Obtener el ID del documento
                    newNovels.add(novel);

                    // Guardamos la novela también en SQLite
                    sqliteHelper.addNovel(novel);
                }

                updateNovelList(newNovels);
            } else {
                Log.d("Firebase", "Error al obtener novelas: ", task.getException());
            }
        });
    }

    private void updateNovelList(List<Novel> newNovels) {
        // Limpiamos el layout antes de mostrar las novelas
        novelsLayout.removeAllViews();

        // Usamos un Set para evitar duplicados
        Set<String> existingNovelIds = new HashSet<>();

        // Agregamos las nuevas novelas al layout
        for (Novel novel : newNovels) {
            if (!existingNovelIds.contains(novel.getId())) {
                existingNovelIds.add(novel.getId());
                displayNovel(novel);
            }
        }
    }

    private void updateFavoriteStatus(Novel novel) {
        db.collection("novelas").document(novel.getId()).update("favorite", novel.isFavorite())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Estado de favorito actualizado: " + novel.isFavorite());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al actualizar el estado de favorito", e);
                });

        // Actualizamos el estado de favorito también en SQLite
        sqliteHelper.updateFavoriteStatus(novel.getId(), novel.isFavorite());
    }

    @SuppressLint("SetTextI18n")
    private void displayNovel(Novel novel) {
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardView.setPadding(16, 16, 16, 16);
        cardView.setCardElevation(4);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

        TextView novelView = new TextView(this);
        novelView.setText(novel.getTitle() + "\n" + novel.getAuthor());
        novelView.setPadding(16, 16, 16, 16);
        novelView.setTextSize(18);

        Button favoriteButton = new Button(this);
        favoriteButton.setText(novel.isFavorite() ? "Eliminar de Favoritos" : "Añadir a Favoritos");
        favoriteButton.setOnClickListener(v -> {
            novel.setFavorite(!novel.isFavorite());
            updateFavoriteStatus(novel);
            favoriteButton.setText(novel.isFavorite() ? "Eliminar de Favoritos" : "Añadir a Favoritos");
        });

        Button reviewButton = new Button(this);
        reviewButton.setText("Generar Reseña");
        reviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipalActivity.this, AddReviewActivity.class);
            intent.putExtra("EXTRA_NOVEL_ID", novel.getId());
            intent.putExtra("EXTRA_NOVEL_NAME", novel.getTitle());
            startActivity(intent);
        });

        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.addView(novelView);
        cardLayout.addView(favoriteButton);
        cardLayout.addView(reviewButton);
        cardView.addView(cardLayout);

        novelsLayout.addView(cardView);
    }

    private void startPolling() {
        while (true) {
            try {
                Thread.sleep(5000); // Espera de 5 sec
                runOnUiThread(this::loadNovels); // Cargamos las novelas en el hilo principal
            } catch (InterruptedException e) {
                Log.e("Polling", "Error al dormir el hilo", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
