package com.planazo.config;

import com.planazo.model.Category;
import com.planazo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        loadCategories();
    }

    private void loadCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already loaded. Skipping seed...");
            return;
        }

        log.info("Loading initial categories...");

        List<Category> categories = Arrays.asList(
                Category.builder()
                        .name("Deportes")
                        .description("Actividades deportivas y ejercicio")
                        .iconEmoji("⚽")
                        .colorHex("#10B981")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Cafés y Comida")
                        .description("Encuentros para tomar algo o comer")
                        .iconEmoji("☕")
                        .colorHex("#F59E0B")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Cultura")
                        .description("Museos, exposiciones y eventos culturales")
                        .iconEmoji("🎭")
                        .colorHex("#8B5CF6")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Música y Conciertos")
                        .description("Conciertos, festivales y eventos musicales")
                        .iconEmoji("🎵")
                        .colorHex("#EC4899")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Naturaleza y Senderismo")
                        .description("Excursiones, rutas y actividades al aire libre")
                        .iconEmoji("🌲")
                        .colorHex("#059669")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Juegos de Mesa")
                        .description("Quedadas para jugar juegos de mesa")
                        .iconEmoji("🎲")
                        .colorHex("#EF4444")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Cine y Series")
                        .description("Ver películas o series en grupo")
                        .iconEmoji("🎬")
                        .colorHex("#3B82F6")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Viajes")
                        .description("Escapadas y viajes en grupo")
                        .iconEmoji("✈️")
                        .colorHex("#06B6D4")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Fiestas y Eventos")
                        .description("Celebraciones y eventos sociales")
                        .iconEmoji("🎉")
                        .colorHex("#F97316")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Estudio y Trabajo")
                        .description("Sesiones de estudio o coworking")
                        .iconEmoji("📚")
                        .colorHex("#6366F1")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Mascotas")
                        .description("Quedadas con mascotas")
                        .iconEmoji("🐕")
                        .colorHex("#84CC16")
                        .active(true)
                        .build(),

                Category.builder()
                        .name("Otros")
                        .description("Otras actividades")
                        .iconEmoji("🌟")
                        .colorHex("#6B7280")
                        .active(true)
                        .build()
        );

        categoryRepository.saveAll(categories);
        log.info("✅ {} categories loaded successfully", categories.size());
    }
}
