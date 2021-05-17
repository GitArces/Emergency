package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;

public class Simulator {

	// Coda degli eventi
	private PriorityQueue<Event> queue;

	// Modello del mondo
	private List<Patient> patients;
	private int freeStudios; // numero studi liberi

	private Patient.ColorCode ultimoColore;

	// Parametri di input
	private int totStudios = 3; // NS

	private int numPatients = 120; // NP
	private Duration T_ARRIVAL = Duration.ofMinutes(5);

	private Duration DURATION_TRIAGE = Duration.ofMinutes(5);
	private Duration DURATION_WHITE = Duration.ofMinutes(10);
	private Duration DURATION_YELLOW = Duration.ofMinutes(15);
	private Duration DURATION_RED = Duration.ofMinutes(30);

	private Duration TIMEOUT_WHITE = Duration.ofMinutes(60);
	private Duration TIMEOUT_YELLOW = Duration.ofMinutes(30);
	private Duration TIMEOUT_RED = Duration.ofMinutes(30);

	private LocalTime startTime = LocalTime.of(8, 00);
	private LocalTime endTime = LocalTime.of(20, 00);

	// Parametri di output
	private int patientsTreated;
	private int patientsAbandoned;
	private int patientsDead;

	// INIZIALIZZA IL SIMULATORE e crea gli eventi iniziali
	public void init() {
		// inizializza coda eventi
		this.queue = new PriorityQueue<>();

		// inizializza modello del mondo
		this.patients = new ArrayList<>();
		this.freeStudios = this.totStudios;

		this.ultimoColore = ColorCode.RED;

		// inizializza i parametri di output
		this.patientsAbandoned = 0;
		this.patientsDead = 0;
		this.patientsTreated = 0;

		// inietta gli eventi di input (ARRIVAL)

		LocalTime ora = this.startTime;
		int inseriti = 0;
//		Patient.ColorCode colore = ColorCode.WHITE ;

		while (ora.isBefore(this.endTime) && inseriti < this.numPatients) {
			Patient p = new Patient(ora, ColorCode.NEW);

			Event e = new Event(ora, EventType.ARRIVAL, p);

			this.queue.add(e);
			this.patients.add(p);

			inseriti++;
			ora = ora.plus(T_ARRIVAL);

			/*
				*/
		}

	}

	private Patient.ColorCode prossimoColore() {
		if (ultimoColore.equals(ColorCode.WHITE))
			ultimoColore = ColorCode.YELLOW;
		else if (ultimoColore.equals(ColorCode.YELLOW))
			ultimoColore = ColorCode.RED;
		else
			ultimoColore = ColorCode.WHITE;
		return ultimoColore;
	}

	// ESEGUE LA SIMULAZIONE
	public void run() {
		while (!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
	}

	private void processEvent(Event e) {

		Patient p = e.getPatient();
		LocalTime ora = e.getTime();
		Patient.ColorCode colore = p.getColor();

		switch (e.getType()) {
		case ARRIVAL:
			this.queue.add(new Event(ora.plus(DURATION_TRIAGE), EventType.TRIAGE, p));
			break;

		case TRIAGE:
			p.setColor(prossimoColore());
			if (p.getColor().equals(Patient.ColorCode.WHITE))
				this.queue.add(new Event(ora.plus(TIMEOUT_WHITE), EventType.TIMEOUT, p));
			else if (p.getColor().equals(Patient.ColorCode.YELLOW))
				this.queue.add(new Event(ora.plus(TIMEOUT_YELLOW), EventType.TIMEOUT, p));
			else if (p.getColor().equals(Patient.ColorCode.RED))
				this.queue.add(new Event(ora.plus(TIMEOUT_RED), EventType.TIMEOUT, p));
			break;

		case FREE_STUDIO:
			break;

		case TIMEOUT:
			switch (colore) {
			case WHITE:
				p.setColor(ColorCode.OUT);
				this.patientsAbandoned++ ;
				break;
				
			case YELLOW:
				p.setColor(ColorCode.RED);
				this.queue.add(new Event(ora.plus(TIMEOUT_RED), EventType.TIMEOUT, p));
				break;
				
			case RED:
				p.setColor(ColorCode.BLACK);
				this.patientsDead++ ;
				break;
				
			default:
				System.out.println("ERRORE: TIMEOUT CON COLORE "+colore) ;
			}
			break;

		case TREATED:
			break;
		}

	}

	public void setTotStudios(int totStudios) {
		this.totStudios = totStudios;
	}

	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}

	public void setT_ARRIVAL(Duration t_ARRIVAL) {
		T_ARRIVAL = t_ARRIVAL;
	}

	public void setDURATION_TRIAGE(Duration dURATION_TRIAGE) {
		DURATION_TRIAGE = dURATION_TRIAGE;
	}

	public void setDURATION_WHITE(Duration dURATION_WHITE) {
		DURATION_WHITE = dURATION_WHITE;
	}

	public void setDURATION_YELLOW(Duration dURATION_YELLOW) {
		DURATION_YELLOW = dURATION_YELLOW;
	}

	public void setDURATION_RED(Duration dURATION_RED) {
		DURATION_RED = dURATION_RED;
	}

	public void setTIMEOUT_WHITE(Duration tIMEOUT_WHITE) {
		TIMEOUT_WHITE = tIMEOUT_WHITE;
	}

	public void setTIMEOUT_YELLOW(Duration tIMEOUT_YELLOW) {
		TIMEOUT_YELLOW = tIMEOUT_YELLOW;
	}

	public void setTIMEOUT_RED(Duration tIMEOUT_RED) {
		TIMEOUT_RED = tIMEOUT_RED;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public int getPatientsTreated() {
		return patientsTreated;
	}

	public int getPatientsAbandoned() {
		return patientsAbandoned;
	}

	public int getPatientsDead() {
		return patientsDead;
	}

}
