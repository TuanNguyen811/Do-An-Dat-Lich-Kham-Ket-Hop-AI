import yagmail
import random


import logging
#Cau hinh log
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

#Tao ma OTP ngau nhien
def generate_otp(length=6):
    otp = ""
    for _ in range(length):
        digit = random.randint(0, 9)
        otp += str(digit)
    return otp


def send_otp_utils(receiver_email, otp_code):
    # Thong tin gmail cua ban
    email = ""
    app_password = ""
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


# def send_with_otp(receiver_email: str):
#     otp = generate_otp()
#     send_otp(receiver_email=receiver_email, otp_code=otp)
#
#
# send_with_otp(receiver_email=to)
