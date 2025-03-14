package com.example.beauty_salon_booking;

import com.example.beauty_salon_booking.entities.Appointment;
import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.entities.BeautyService;
import com.example.beauty_salon_booking.enums.AppointmentStatus;
import com.example.beauty_salon_booking.services.AppointmentService;
import com.example.beauty_salon_booking.services.ClientService;
import com.example.beauty_salon_booking.services.MasterService;
import com.example.beauty_salon_booking.services.BeautyServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private BeautyServiceService beautyServiceService;

    private Client client;
    private Master master;
    private BeautyService beautyService;

    @BeforeEach
    public void setUp() {
        // Создаем тестовые данные
        System.out.println("Setting up the test...\n");

        client = new Client();
        client.setName("Иван");
        client.setPhone("+79001112233");
        client.setLogin("ivan");
        client.setPassword("password1");

        master = new Master();
        master.setName("Алексей");
        master.setPhone("+79007778899");
        master.setLogin("alexey");
        master.setPassword("password3");

        beautyService = new BeautyService();
        beautyService.setName("Стрижка");
        beautyService.setPrice(1500.00);

        // Сохраняем тестовые данные
        client = clientService.saveClient(client);
        master = masterService.saveMaster(master);
        beautyService = beautyServiceService.saveBeautyService(beautyService);
    }

    @Test
    public void testCreateAndUpdateAppointment() {
        System.out.println("Running testCreateAndUpdateAppointment...\n");

        // Сначала создаем новый объект Appointment
        System.out.println("Creating a new object and saving it...\n");
        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setMaster(master);
        appointment.setBeautyService(beautyService);
        appointment.setDate(LocalDate.parse("2025-03-07"));
        appointment.setTime(LocalTime.parse("12:00"));
        appointment.setStatus(AppointmentStatus.valueOf("CANCELED"));

        // Сохраняем его
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        // Проверяем, что объект был сохранен в базе данных
        assertNotNull(savedAppointment.getId(), "Appointment should be saved with an ID.\n");

        System.out.println("The object has been created and saved!\n");

        // Теперь извлекаем объект из базы данных по ID и изменяем его
        System.out.println("Extracting an object...\n");

        Appointment appointmentFromDb = appointmentService.getAppointmentById(savedAppointment.getId()).orElseThrow();

        System.out.println("The extraction of the object from the DB was successful.\n"+ "The appointmentFromDb has values: " +
                appointmentFromDb.getId() + ", " + appointmentFromDb.getClient().getName() + ", " +
                appointmentFromDb.getMaster().getName() + ", " + appointmentFromDb.getBeautyService().getName() + ", " +
                appointmentFromDb.getDate() + ", " + appointmentFromDb.getTime() + ", " + appointmentFromDb.getStatus() + "\n");

        System.out.println("Modifying an object...\n");
        appointmentFromDb.setTime(LocalTime.parse("14:00"));
        appointmentFromDb.setStatus(AppointmentStatus.valueOf("CONFIRMED"));

        // Сохраняем изменения
        System.out.println("Saving the modified object in the DB...\n");
        Appointment updatedAppointment = appointmentService.saveAppointment(appointmentFromDb);

        // Проверяем, что изменения были сохранены
        assertEquals(LocalTime.parse("14:00"), updatedAppointment.getTime(), "Appointment time should be updated.");
        assertEquals(AppointmentStatus.valueOf("CONFIRMED"), updatedAppointment.getStatus(), "Appointment status should be updated.");

        System.out.println("The object was successfully modified and saved!\n"+ "The modified object has: " +
                appointmentFromDb.getId() + ", " + appointmentFromDb.getClient().getName() + ", " +
                appointmentFromDb.getMaster().getName() + ", " + appointmentFromDb.getBeautyService().getName() + ", " +
                appointmentFromDb.getDate() + ", " + appointmentFromDb.getTime() + ", " + appointmentFromDb.getStatus() + "\n");
    }
}