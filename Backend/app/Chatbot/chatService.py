import logging
import google.generativeai as genai
from typing import Dict, List

from datetime import datetime, timezone  # Ranh qua tinh time choi
from zoneinfo import ZoneInfo #Lay zone vietname
#Cau hinh ghi logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")
logger = logging.getLogger(__name__)

#Luu session cua nguoi dung o trong RAM
#chat_session = {} #key: str, value ChatSession
chat_sessions: Dict[str, genai.ChatSession] = {} #key: str, value ChatSession

#Cau hinh API key
def check_configure_genai(api_key: str) -> None:
    try:
        genai.configure(api_key=api_key)
        logger.info("Đã cấu hình API key thành công!!!!")
    except Exception as e:
        logger.error(f"Lỗi kh cấu hình API key: {e}")
        raise

#Lay ChatSession - neu co thi khong tao, khong co thi create
def get_chat_session(user_id: int, model_input: genai.GenerativeModel):
    #Check user_id khong duoc rong va phai la str
    if not user_id or not isinstance(user_id, int):
        raise ValueError("user_id phải là một chuỗi không rỗng")
    if user_id not in chat_sessions:
        try:
            chat_sessions[user_id] = model_input.start_chat(history=[])
            logger.info(f"Đã tạo phiên bản chat mới cho user_id: {user_id}")
        except Exception as e:
            logger.error(f"Lỗi khi tạo phiên bản chat mới cho user_id {user_id}, {e}")
    logger.info(f"Đã tạo phiên bản chat cho user_id {user_id}")
    return chat_sessions[user_id]


#Rảnh quá không có gì chơi viết chơi chơi
def get_time_utc() -> datetime:
    return datetime.now(timezone.utc)

def get_time_vn(utc_time: datetime) -> datetime: #Doi gio utc sang vn(utc+7)
    zone_vn = "Asia/Ho_Chi_Minh"
    viet_name_time = utc_time.astimezone(ZoneInfo(zone_vn))
    return viet_name_time

#Viet function gui message to Gemini
def send_user_message_to_gemini(user_id: int, message: str, model_input: genai.GenerativeModel) -> Dict[str, str]:
    if not message: #gui message rong hot lien
        raise ValueError("message: phải là một chuỗi không rỗng")
    try:
        logger.info(f"Đang chuẩn bị send message to Gemni")
        chat = get_chat_session(user_id, model_input) #Lay chat session
        response = chat.send_message(message)
        current_time = get_time_utc()

        return {
            "reply": response.text,
            "time_utc": current_time.isoformat(),
            "time_vn": get_time_vn(current_time).isoformat()
        }
    except Exception as e:
        logger.error(f"Lỗi khi gửi tin nhắn tới gemni: {e}")
        return {
            "error": str(e),
            "time": get_time_vn(get_time_utc()).isoformat()
        }


def get_chat_history(user_id: int, model_input: genai.GenerativeModel) -> List[Dict[str, str]]:
    try:
        list_history = []
        chat_with_user_id = get_chat_session(user_id, model_input)
        history = chat_with_user_id.history

        for x in history:
            diction_example = {"role": x.role, "text": x.parts[0].text}
            list_history.append(diction_example)
        return list_history
    except Exception as e:
        logger.error(f"Lỗi khi lấy lịch sử chat: {e}")
        return []