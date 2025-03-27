package com.example.beauty_salon_booking;

import com.example.beauty_salon_booking.dto.AppointmentDTO;
import com.example.beauty_salon_booking.dto.MasterDTO;
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
import java.util.*;

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

    @BeforeEach
    public void setUp() {
        // Создаем тестовые данные
        System.out.println("Setting up the test...\n");

        BeautyService beautyService1 = new BeautyService();
        beautyService1.setName("Stilist");
        beautyService1.setPrice(1500.00);
        beautyService1.setDescription("Pricheska");
        List<Master> masters1 = new ArrayList<>();
        beautyService1.setMasters(masters1);
        beautyServiceService.saveBeautyService(beautyService1);

        Client client1 = new Client();
        client1.setName("Ivan");
        client1.setPhone("+79000000000");
        client1.setLogin("ivan");
        client1.setPassword("password1");
        clientService.saveClient(client1);

        Client client2 = new Client();
        client2.setName("Petr");
        client2.setPhone("+79111111119");
        client2.setLogin("petr");
        client2.setPassword("password2");
        clientService.saveClient(client2);

        Client client3 = new Client();
        client3.setName("Vladimir");
        client3.setPhone("+79222222229");
        client3.setLogin("vladimir");
        client3.setPassword("password3");
        clientService.saveClient(client3);

        Master master1 = new Master();
        master1.setName("Alexey");
        master1.setPhone("+79333333339");
        master1.setLogin("alexey");
        master1.setPassword("password4");
        List<BeautyService> beautyServices1 = new ArrayList<>();
        master1.setBeautyServices(beautyServices1);
        masterService.saveMaster(master1);
        masterService.addBeautyServiceToMaster(1L, 1L);

        Master master2 = new Master();
        master2.setName("Mihail");
        master2.setPhone("+79444444449");
        master2.setLogin("mihail");
        master2.setPassword("password5");
        List<BeautyService> beautyServices2 = new ArrayList<>();
        master2.setBeautyServices(beautyServices2);
        masterService.saveMaster(master2);
        masterService.addBeautyServiceToMaster(2L, 1L);

        // Сначала создаем новый объект Appointment
        System.out.println("Creating a new object and saving it...\n");

        Appointment appointment1 = new Appointment();
        appointment1.setClient(client1);
        appointment1.setMaster(master1);
        appointment1.setBeautyService(beautyService1);
        appointment1.setDate(LocalDate.parse("2025-03-07"));
        appointment1.setTime(LocalTime.parse("12:00"));
        appointment1.setStatus(AppointmentStatus.valueOf("CANCELED"));

        // Сохраняем его
        appointmentService.saveAppointment(appointment1);

        // Сначала создаем новый объект Appointment
        System.out.println("Creating a new object and saving it...\n");
        Appointment appointment2 = new Appointment();
        appointment2.setClient(client2);
        appointment2.setMaster(master2);
        appointment2.setBeautyService(beautyService1);
        appointment2.setDate(LocalDate.parse("2025-03-07"));
        appointment2.setTime(LocalTime.parse("12:00"));
        appointment2.setStatus(AppointmentStatus.valueOf("CANCELED"));

        // Сохраняем его
        appointmentService.saveAppointment(appointment2);

        // Сначала создаем новый объект Appointment
        System.out.println("Creating a new object and saving it...\n");
        Appointment appointment3 = new Appointment();
        appointment3.setClient(client3);
        appointment3.setMaster(master2);
        appointment3.setBeautyService(beautyService1);
        appointment3.setDate(LocalDate.parse("2025-03-07"));
        appointment3.setTime(LocalTime.parse("12:00"));
        appointment3.setStatus(AppointmentStatus.valueOf("CANCELED"));

        // Сохраняем его
        appointmentService.saveAppointment(appointment3);
    }

    @Test
    public void testMostPopularMaster() {
        System.out.println("Running testMostPopularMaster...\n");

        List<AppointmentDTO> allAppointments = appointmentService.getAllAppointments();
        List<AppointmentDTO> filteredAppointments = new ArrayList<>();
        for (AppointmentDTO appointment : allAppointments) {
            if (appointment.getBeautyService().getName().equals("Stilist")) {
                filteredAppointments.add(appointment);
            }
        }

        Map<Long, Integer> masterCountMap = new HashMap<>();
        for (AppointmentDTO appointment : filteredAppointments) {
            Long masterId = appointment.getMaster().getId();
            if (masterCountMap.containsKey(masterId)) {
                masterCountMap.put(masterId, masterCountMap.get(masterId) + 1);
            } else {
                masterCountMap.put(masterId, 1);
            }
        }

        List<Map.Entry<Long, Integer>> sortedMasters = new ArrayList<>(masterCountMap.entrySet());
        for (int i = 0; i < sortedMasters.size() - 1; i++) {
            for (int j = 0; j < sortedMasters.size() - i - 1; j++) {
                if (sortedMasters.get(j).getValue() < sortedMasters.get(j + 1).getValue()) {
                    Map.Entry<Long, Integer> temp = sortedMasters.get(j);
                    sortedMasters.set(j, sortedMasters.get(j + 1));
                    sortedMasters.set(j + 1, temp);
                }
            }
        }

        System.out.println("Мастера по популярности:");
        for (Map.Entry<Long, Integer> entry : sortedMasters) {
            MasterDTO master = masterService.getMasterById(entry.getKey()).orElseThrow(() -> new IllegalArgumentException("Мастер не найден!"));
            System.out.println("Мастер: " + master.getName() + ", Количество записей: " + entry.getValue());
        }
    }
}