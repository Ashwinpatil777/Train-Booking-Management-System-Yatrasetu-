# Train Booking Management System - YatraSetu

A full-stack **Train Booking Management System** that allows users to search trains, select seats, book tickets, and make payments using **Stripe**. Built with **Spring Boot** for the backend and **React.js** for the frontend.  

---

## Table of Contents

- [Features](#features)
- [User Workflow](#user-workflow)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Environment Variables](#environment-variables)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### User Features:
- Search trains by **source, destination, and date**
- Select a train and choose a **coach**
- View seat layout in each coach (**100 seats per coach**)
- Select **specific seats**
- Enter **customer details** for the selected seats
- Make **payment via Stripe**
- Booking confirmation with **email notification**
- Download/print **ticket PDF**

### Admin Features:
- Manage trains (Add, Update, Delete)
- Manage coaches and seat availability
- View all bookings and users

---

## User Workflow

1. **Search Train**: User selects a date, source, and destination to view available trains.
2. **Select Train**: User chooses a train from the available list.
3. **Select Coach & Seats**: User selects a coach and can pick specific seats from a layout of 100 seats.
4. **Add Customer Details**: User enters passenger details for the selected seats.
5. **Payment**: User pays using **Stripe Payment Gateway**.
6. **Confirmation**: After successful payment:
   - Booking is confirmed
   - User receives a **confirmation email**
   - PDF of the ticket can be downloaded or printed

---

## Technologies

**Backend:**
- Java 17, Spring Boot, Hibernate
- RESTful APIs
- MySQL Database
- Stripe Payment Integration
- Email Notification

**Frontend:**
- React.js, React Bootstrap
- Axios for API calls
- Responsive UI

**Others:**
- Git & GitHub
- PDF generation for tickets

---

## Project Structure

