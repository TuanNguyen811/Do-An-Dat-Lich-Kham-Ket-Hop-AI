import yagmail
import random
from dotenv import load_dotenv
import os
load_dotenv(os.path.join(os.path.dirname(__file__), "..", "config", "config.env"))

import logging
#Cau hinh log
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)
#hi
#Tao ma OTP ngau nhien
def generate_otp(length=6):
    otp = ""
    for _ in range(length):
        digit = random.randint(0, 9)
        otp += str(digit)
    return otp


def send_otp_utils(receiver_email, otp_code):
    # Thong tin gmail cua ban
    email = os.getenv("GMAIL")
    app_password = os.getenv("PASSWORD")

    # Khoi tao yagmail
    yag = yagmail.SMTP(user=email, password=app_password)

    subject = "Mã OTP xác thực của bạn"
    content = f"""
        Chào bạn,

        Mã OTP của bạn là: {otp_code}

        Vui lòng không chia sẻ mã này với bất kỳ ai.

        Trân trọng,
        Hệ thống xác thực
        """

    yag.send(to=receiver_email, subject=subject, contents=content)
    logger.info(f"Email đã gửi thành công đến: {receiver_email}")

