from typing import Dict, Any

from fastapi import FastAPI, HTTPException, APIRouter, Depends
import google.generativeai as genai
from pydantic import BaseModel, Field #Xac thuc du lieu dau vao

from Oauth import deps
from .chatService import (
    check_configure_genai,
    send_user_message_to_gemini,
    get_chat_history, chat_sessions
)

import logging
#Cau hinh log
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

#Cau hinh API key
check_configure_genai(api_key="")
model = genai.GenerativeModel("gemini-1.5-flash")


router = APIRouter( prefix="", tags=["CHAT BOT WITH API KEY GEMINI"])

#Models
class ChatRequest(BaseModel):
    message: str = Field(..., description="Nội dung của tin nhắn")


class HistoryRequest(BaseModel):
    user_id: str
#Routes
@router.post("/chat")
async def chat_with_bot(
        request: ChatRequest,
        current_user: Dict[str, Any] = Depends(deps.get_current_user)
):
    user_id = current_user.get("user_id")
    try:
        logger.info(f"Nhận tin nhắn từ user_id: {user_id}")
        result = send_user_message_to_gemini(user_id, request.message, model)
        return result
    except Exception as e:
        logger.error(f"Lỗi khi xử lý tin nhắn: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/chat/history")
async def get_history(
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
):
    user_id = current_user.get("user_id")
    full_name = current_user.get("full_name")

    try:
        logger.info(f"Lấy lịch sử chat cho : {full_name}")
        history = get_chat_history(user_id, model)
        return [{"full_name": full_name, "history_chat": history}]
    except Exception as e:
        logger.error(f"Lỗi khi lấy lịch sử chat: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/chat/reset_session")
async def reset_chat_session(
        current_user: Dict[str, Any] = Depends(deps.get_current_user)
):
    user_id = current_user.get("user_id")
    if not user_id or not isinstance(user_id, int):
        raise HTTPException(status_code=400, detail="Invalid user_id: must be a non-empty integer")

    try:
        # Delete the current session if it exists
        if user_id in chat_sessions:
            del chat_sessions[user_id]
            logger.info(f"Đã xóa phiên bản chat hiện tại cho user_id: {user_id}")

        # Create a new session
        chat_sessions[user_id] = model.start_chat(history=[])
        logger.info(f"Đã tạo phiên bản chat mới cho user_id: {user_id}")

        return {"message": "Chat session reset successfully", "user_id": user_id}
    except Exception as e:
        logger.error(f"Lỗi khi reset phiên bản chat cho user_id {user_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Failed to reset chat session: {str(e)}")
