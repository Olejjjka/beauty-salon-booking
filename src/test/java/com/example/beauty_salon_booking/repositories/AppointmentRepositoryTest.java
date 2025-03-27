package com.example.beauty_salon_booking.repositories;

import com.example.beauty_salon_booking.entities.Appointment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // Тест только для слоя JPA (без запуска всего приложения)
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    public void testFindByClientId() {
        // Достаем записи клиента с id = 1
        List<Appointment> appointments = appointmentRepository.findByClientId(1L);

        // Проверяем, что записи существуют
        assertThat(appointments).isNotEmpty();

        // Выводим в лог первую запись (если есть)
        appointments.forEach(System.out::println);
    }
}
