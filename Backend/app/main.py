from fastapi import FastAPI
from starlette.staticfiles import StaticFiles
from fastapi.middleware.cors import CORSMiddleware
import os

from API import (
    appointments,
    auth,
    departments,
    doctors,
    medical_history,
    notifications,
    patients,
    users,
    admin,
    doctor_schedule
)
import google.generativeai as genai

from Chatbot import api_chatbot

app = FastAPI()

# Include all routers
app.include_router(auth.router)
app.include_router(doctors.router)
app.include_router(patients.router)
app.include_router(users.router)
app.include_router(admin.router)
app.include_router(departments.router)
app.include_router(appointments.router)
app.include_router(medical_history.router)
app.include_router(notifications.router)
app.include_router(doctor_schedule.router)
app.include_router(api_chatbot.router)

from database.database import create_tables

# Setup CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins
    allow_credentials=True,
    allow_methods=["*"],  # Allows all methods
    allow_headers=["*"],  # Allows all headers
)

# Create tables
create_tables()

# Setup static file directory
AVATAR_DIR = os.path.join(os.getcwd(), "data", "avatars")
os.makedirs(AVATAR_DIR, exist_ok=True)
app.mount("/avatars", StaticFiles(directory=AVATAR_DIR), name="avatars")

# Root endpoint
@app.get("/")
def read_root():
    return {"message": "Welcome to the Medical API"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)