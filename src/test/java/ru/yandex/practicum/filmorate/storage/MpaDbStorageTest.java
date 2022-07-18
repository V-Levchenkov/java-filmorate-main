package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:beforeEachTest.sql")
class MpaDbStorageTest {
    private final MpaStorage mpaStorage;

    @Test
    void getMPAByIdTest() {
        assertThat(mpaStorage.getMpaById(1L).getName()).isEqualTo("G");
        assertThat(mpaStorage.getMpaById(2L).getName()).isEqualTo("PG");
        assertThat(mpaStorage.getMpaById(3L).getName()).isEqualTo("PG-13");
        assertThat(mpaStorage.getMpaById(4L).getName()).isEqualTo("R");
        assertThat(mpaStorage.getMpaById(5L).getName()).isEqualTo("NC-17");
    }

    @Test
    void getMPAValuesTest() {
        List<MpaRating> allMPAs = new ArrayList<>();
        allMPAs.add(new MpaRating(1L, "G"));
        allMPAs.add(new MpaRating(2L, "PG"));
        allMPAs.add(new MpaRating(3L, "PG-13"));
        allMPAs.add(new MpaRating(4L, "R"));
        allMPAs.add(new MpaRating(5L, "NC-17"));
        assertThat(mpaStorage.getMpaValues()).isEqualTo(allMPAs);
    }
}