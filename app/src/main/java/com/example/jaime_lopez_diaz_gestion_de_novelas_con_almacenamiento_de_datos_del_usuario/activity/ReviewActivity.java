package com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.R;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.domain.Novel;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.domain.Review;
import com.example.jaime_lopez_diaz_gestion_de_novelas_con_almacenamiento_de_datos_del_usuario.ui.review.ReviewViewModel;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private ReviewViewModel reviewViewModel;
    private LinearLayout reviewsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewsLayout = findViewById(R.id.reviews_layout);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        // Obtener todas las reseñas
        loadAllReviews();
    }

    private void loadAllReviews() {
        reviewViewModel.getAllReviews().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                displayReviews(reviews);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayReviews(List<Review> reviews) {
        reviewsLayout.removeAllViews();
        for (Review review : reviews) {
            TextView reviewView = new TextView(this);
            reviewView.setText("Usuario: " + review.getReviewer() + "\n" +
                    "Comentario: " + review.getComment() + "\n" +
                    "Puntuación: " + review.getRating() + "\n" +
                    "Nombre de la novela: " + review.getNovelName());
            reviewView.setPadding(16, 16, 16, 16);

            // Botón para ver más detalles de la novela
            Button viewNovelButton = new Button(this);
            viewNovelButton.setText("Ver Novela");
            viewNovelButton.setOnClickListener(v -> loadNovelDetails(review.getNovelId())); // Cargar detalles de la novela

            reviewsLayout.addView(reviewView);
            reviewsLayout.addView(viewNovelButton);
        }

        if (reviews.isEmpty()) {
            TextView noReviewsView = new TextView(this);
            noReviewsView.setText("No hay reseñas disponibles.");
            noReviewsView.setPadding(16, 16, 16, 16);
            reviewsLayout.addView(noReviewsView);
        }
    }

    private void loadNovelDetails(String novelId) {
        reviewViewModel.getNovelById(novelId).observe(this, new Observer<Novel>() {
            @Override
            public void onChanged(Novel novel) {
                if (novel != null) {
                    showNovelDetails(novel);
                }
            }
        });
    }

    private void showNovelDetails(Novel novel) {
        Toast.makeText(this, "Título: " + novel.getTitle() + "\nAutor: " + novel.getAuthor(), Toast.LENGTH_LONG).show();
    }
}

