import yagmail
import random


import logging

from Email.template_email import get_subject_otp, get_content_template_otp, get_content_appointment

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

#Gui email voi muc dich gi thi dung content do
def send_email_utils(receiver_email, content_template_html):
    # Thong tin gmail cua ban
    email = ""
    app_password = ""
    # Khoi tao yagmail
    yag = yagmail.SMTP(user=email, password=app_password)

    subject = get_subject_otp()
    html_content = content_template_html

    yag.send(to=receiver_email, subject=subject, contents=html_content)
    logger.info(f"Email đã gửi thành công đến: {receiver_email}")


def send_with_otp(receiver_email: str):
    otp = generate_otp()
    content_otp = get_content_template_otp(otp_code=otp, full_name="Trum Thái")
    send_email_utils(receiver_email=receiver_email, content_template_html=content_otp)

def send_with_appointment(receiver_email: str):
    content_appointment = get_content_appointment(
        appointment_date="16/04/2025",
        description="Bị đau bụng nên đặt lịch khám",
        doctor_name="Nguyễn An",
        department_name="Khoa Dạ Dày",
        shift="7h-8h",
        status="Scheduled"

    )
    send_email_utils(receiver_email=receiver_email, content_template_html=content_appointment)

# send_with_otp(receiver_email="trumthai10052004@gmail.com")
send_with_appointment(receiver_email="trumthai10052004@gmail.com")