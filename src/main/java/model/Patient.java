package model;

import java.time.LocalTime;
import java.util.Objects;

public class Patient implements Comparable<Patient> {
	
	@Override
	public String toString() {
		return "Patient [num=" + num + ", arrivalTime=" + arrivalTime + ", color=" + color + "]";
	}
	
	public enum ColorCode {
		NEW, // in triage
		WHITE, YELLOW, RED, BLACK, // sala d'attesa
		TREATING, // nello studio medico
		OUT // a casa (abbandonato o curato)
	};
	
	private int num;
	
	private LocalTime arrivalTime;
	private ColorCode color;
	
	public Patient(int num, LocalTime arrivalTime, ColorCode color) {
		super();
		this.num = num;
		this.arrivalTime = arrivalTime;
		this.color = color;
	}
	
	
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public ColorCode getColor() {
		return color;
	}
	public void setColor(ColorCode color) {
		this.color = color;
	}


	@Override
	public int hashCode() {
		return Objects.hash(num);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		return num == other.num;
	}


	@Override
	public int compareTo(Patient other) {
		if (this.color.equals(other.color))
			return this.arrivalTime.compareTo(other.arrivalTime);
		else if (this.color.equals(Patient.ColorCode.RED))
			return -1;
		else if(other.color.equals(Patient.ColorCode.RED))
			return +1;
		else if (this.color.equals(Patient.ColorCode.YELLOW))  // Y - W
			return -1;
		else    // W - Y
			return +1;
		
	}
	
	
	
}
