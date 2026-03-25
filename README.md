# STRIDE AI – Backend

Spring Boot REST API backend for the STRIDE AI intelligent fitness tracking system.

## Tech Stack
- Spring Boot 3.2 (Java 17)
- Spring Security + JWT (jjwt 0.11)
- Spring Data JPA + Hibernate
- H2 (dev) / PostgreSQL (prod)
- Lombok

## Quick Start (H2 in-memory – no DB setup needed)

```bash
mvn spring-boot:run
```

API runs at http://localhost:8080  
H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:strideai`)

## Production (PostgreSQL)

```bash
# Set environment variables
export DB_URL=jdbc:postgresql://localhost:5432/strideai
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-256-bit-secret-key-here

mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## API Reference

### Auth (no token required)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |

### Users (JWT required)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/me` | Get current user profile |
| PUT | `/api/users/me` | Update profile |

### Exercises
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/exercises` | List all exercises (filter: `?category=Strength&difficulty=Beginner`) |
| GET | `/api/exercises/{id}` | Get exercise details |
| GET | `/api/exercises/{id}/rules` | Get AI rules JSON for exercise |
| POST | `/api/exercises` | Add new exercise (admin) |

### Workout Sessions
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/sessions` | Start a session |
| PUT | `/api/sessions/{id}/complete` | Finish session with summary |
| POST | `/api/sessions/{id}/sets` | Log a completed set |
| GET | `/api/sessions/history` | Paginated history (`?page=0&size=20`) |

### Diet
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/diet/current` | Get current diet plan |
| POST | `/api/diet/generate` | Regenerate plan based on profile |

### Analytics
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/analytics/dashboard` | Full dashboard data |
| GET | `/api/analytics/progress` | Progress trends |
| GET | `/api/analytics/consistency` | Streak data |

## Register Request Body
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "age": 25,
  "weightKg": 75,
  "heightCm": 178,
  "fitnessGoal": "BULK",
  "budgetTier": "MEDIUM",
  "dietPref": "NON_VEG",
  "male": true
}
```

## Exercise Rules JSON Schema
```json
{
  "keypointRulesJson": {
    "rep_counter": {
      "joint": ["left_hip", "left_knee", "left_ankle"],
      "lower_threshold": 95,
      "upper_threshold": 160
    }
  },
  "angleThresholdsJson": [
    {
      "joint": ["left_hip", "left_knee", "left_ankle"],
      "lower": 80,
      "upper": 110,
      "weight": 0.40,
      "cue": "Go deeper – aim for 90° at the knee"
    }
  ],
  "feedbackCuesJson": {
    "good": "Great form!",
    "ok": "Almost there",
    "poor": "Check your form"
  }
}
```

## Project Structure
```
src/main/java/com/strideai/
├── config/          SecurityConfig, DataSeeder, GlobalExceptionHandler
├── controller/      Auth, User, Exercise, WorkoutSession, Diet, Analytics, Plan
├── dto/             AuthDTO, UserDTO, WorkoutDTO
├── model/           User, Exercise, WorkoutSession, SessionSet, DietPlan
├── repository/      JPA interfaces
└── security/        JwtUtils, JwtAuthFilter, UserDetailsServiceImpl
```
