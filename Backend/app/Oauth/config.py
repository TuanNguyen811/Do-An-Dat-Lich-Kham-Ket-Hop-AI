from dotenv import load_dotenv
import os

load_dotenv()  # Load từ .env vào biến môi trường

EMAIL_ADDRESS = os.getenv("EMAIL_ADDRESS")
EMAIL_PASSWORD = os.getenv("EMAIL_PASSWORD")
SECRET_KEY = os.getenv("SECRET_KEY")