#Subject and Content of Email

#Subject of OTP
def get_subject_otp() -> str:
    return "Mã OTP xác thực của bạn"

#Content of OTP
def get_content_template_otp(otp_code, full_name) -> str:
    return f"""
        <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
            <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                <h2 style="color: #333;">Xin chào, {full_name}</h2>
                <p style="font-size: 16px;">Chúng tôi đã nhận được yêu cầu xác thực từ bạn.</p>
                <p style="font-size: 20px; font-weight: bold;">Mã OTP của bạn là:</p>
                <div style="text-align: center; margin: 20px 0;">
                    <span style="color: #d9534f; font-size: 32px; font-weight: bold;">{otp_code}</span>
                </div>
        
                <p style="font-size: 14px; color: #777;">Vui lòng không chia sẻ mã này với bất kỳ ai để đảm bảo an toàn.</p>
                <br>
                <p>Trân trọng,<br>Bệnh viện Bưu Chính 2</p>
            </div>
        </div>
"""



#Subject Appointment
def get_subject_appointment() -> str:
    return "Bệnh viện Bưu Chính 2 - Lịch hẹn của bạn đã đặt thành công"

#Content Appointment
def get_content_appointment(appointment_date,
                            shift,
                            doctor_name,
                            department_name,
                            status,
                            description) -> str:
    return f"""
        <div style="font-family: 'Segoe UI', Arial, sans-serif; padding: 20px; background-color: #f7f7f7;">
            <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 30px; border-radius: 12px; box-shadow: 0 3px 10px rgba(0,0,0,0.1);">
                <!-- Header with Logo -->
                <div style="text-align: center; margin-bottom: 25px;">
                    <div style="display: inline-block; margin-bottom: 15px; width: 120px;">
                        <img src="https://upload.wikimedia.org/wikipedia/commons/1/13/Logo_PTIT_University.png" alt="Logo PTIT" style="width: 100%; height: auto;">
                    </div>
                    <h2 style="color: #c31815; margin: 10px 0; font-weight: 600;">Xác nhận lịch hẹn khám bệnh</h2>
                </div>
                
                <!-- Greeting -->
                <div style="border-left: 4px solid #c31815; padding-left: 15px; margin-bottom: 25px;">
                    <p style="margin: 0; font-size: 16px;">Xin chào quý bệnh nhân,</p>
                    <p style="margin: 10px 0 0 0; font-size: 16px;">Chúng tôi xin thông báo rằng lịch hẹn của bạn đã được <span style="color: #27bd09; font-weight: bold;">đặt thành công</span> với các thông tin sau:</p>
                </div>
                
                <!-- Appointment Details Box -->
                <div style="border-radius: 8px; border: 1px solid #e0e0e0; overflow: hidden; margin: 25px 0;">
                    <div style="background-color: #fef0f0; padding: 15px; text-align: center; border-bottom: 1px solid #e0e0e0;">
                        <h3 style="margin: 0; color: #c31815; font-size: 18px;">Chi tiết lịch hẹn</h3>
                    </div>
                    
                    <table style="width: 100%; border-collapse: collapse;">
                        <tr style="background-color: #f9f9f9;">
                            <td style="padding: 12px 15px; font-weight: 600; color: #555;">Ngày hẹn:</td>
                            <td style="padding: 12px 15px; color: #333;">{appointment_date}</td>
                        </tr>
                        <tr>
                            <td style="padding: 12px 15px; font-weight: 600; color: #555;">Ca làm việc:</td>
                            <td style="padding: 12px 15px; color: #333;">{shift}</td>
                        </tr>
                        <tr style="background-color: #f9f9f9;">
                            <td style="padding: 12px 15px; font-weight: 600; color: #555;">Bác sĩ:</td>
                            <td style="padding: 12px 15px; color: #333;">{doctor_name}</td>
                        </tr>
                        <tr>
                            <td style="padding: 12px 15px; font-weight: 600; color: #555;">Khoa:</td>
                            <td style="padding: 12px 15px; color: #333;">{department_name}</td>
                        </tr>
                        <tr style="background-color: #f9f9f9;">
                            <td style="padding: 12px 15px; font-weight: 600; color: #555;">Trạng thái:</td>
                            <td style="padding: 12px 15px;"><span style="display: inline-block; background-color: #27bd09; color: white; font-weight: 600; padding: 5px 12px; border-radius: 15px; font-size: 14px;">{status}</span></td>
                        </tr>
                        <tr>
                            <td style="padding: 12px 15px; font-weight: 600; color: #555;">Ghi chú:</td>
                            <td style="padding: 12px 15px; color: #333;">{description}</td>
                        </tr>
                    </table>
                </div>
                
                <!-- Important Information -->
                <div style="background-color: #f8f9fa; padding: 15px; border-radius: 8px; margin: 25px 0;">
                    <h4 style="margin: 0 0 10px 0; color: #495057;">Lưu ý quan trọng:</h4>
                    <ul style="margin: 0; padding-left: 20px; color: #555;">
                        <li style="margin-bottom: 5px;">Vui lòng đến trước giờ hẹn 15 phút để hoàn tất thủ tục.</li>
                        <li style="margin-bottom: 5px;">Mang theo giấy tờ tùy thân và thẻ bảo hiểm y tế (nếu có).</li>
                        <li>Nếu không thể đến theo lịch hẹn, vui lòng thông báo cho chúng tôi trước ít nhất 24 giờ.</li>
                    </ul>
                </div>
                
                <!-- Contact Information -->
                <div style="margin-top: 25px; color: #555; font-size: 15px;">
                    <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua:</p>
                    <ul style="list-style-type: none; padding-left: 0; margin: 10px 0;">
                        <li style="margin-bottom: 5px;"><span style="font-weight: 600; margin-right: 5px;">☎️ Hotline:</span>1900 232 460</li>
                        <li><span style="font-weight: 600; margin-right: 5px;">📧 Email:</span> support@benhvienbuuchinh2.com</li>
                    </ul>
                </div>
                
                <!-- Footer -->
                <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; text-align: center; color: #777; font-size: 14px;">
                    <p style="margin-bottom: 10px;">Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>
                    <p style="margin: 0;">Trân trọng,<br><strong>Bệnh viện Bưu Chính 2</strong></p>
                    <div style="margin-top: 15px;">
                        <a href="https://www.facebook.com/" style="display: inline-block; margin: 0 5px;"><img src="https://upload.wikimedia.org/wikipedia/commons/0/05/Facebook_Logo_%282019%29.png" alt="Facebook" style="width: 25px; height: 25px; border-radius: 50%;"></a>
                        <a href="https://youtube.com/" style="display: inline-block; margin: 0 5px;"><img src="https://cdn2.cellphones.com.vn/44x,webp/media/logo/social/cellphones-youtube.png" alt="YouTube" style="width: 25px; height: 25px; border-radius: 50%;"></a>
                        <a href="#" style="display: inline-block; margin: 0 5px;"><img src="https://cdn.haitrieu.com/wp-content/uploads/2022/08/logo-benh-vien-dai-hoc-y-duoc.png" alt="Website" style="width: 25px; height: 25px; border-radius: 50%;"></a>
                    </div>
                </div>
            </div>
        </div>    
    """