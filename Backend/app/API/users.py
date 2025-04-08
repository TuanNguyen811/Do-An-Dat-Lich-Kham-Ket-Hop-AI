from fastapi import FastAPI,APIRouter, Depends, HTTPException, status, File, UploadFile
from fastapi.responses import FileResponse

from sqlalchemy.orm import Session
from typing import List, Optional, Dict, Any

from starlette.staticfiles import StaticFiles

import crud, schemas

from crud.crud_user import set_avatar_user, get_avatar_user
from Oauth import deps

import os
import uuid
import shutil

router = APIRouter(prefix="", tags=["users"])

AVATAR_DIR = os.path.join(os.getcwd(), "data", "avatars")
os.makedirs(AVATAR_DIR, exist_ok=True)

# Mount static files for serving avatars, API Avaters
router.mount("/avatars", StaticFiles(directory=AVATAR_DIR), name="avatars")

@router.post("/user/avatar", status_code=status.HTTP_200_OK)
async def upload_avatar(
        file: UploadFile = File(...),
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    user_id = current_user.get("user_id")

    if not file.filename:
        raise HTTPException(status_code=400, detail="File is required.")

    try:
        # Generate safe file path
        ext = os.path.splitext(file.filename)[1].lower() or ".jpg"
        filename = f"{uuid.uuid4()}{ext}"
        file_path = os.path.join(AVATAR_DIR, filename)

        # Save file
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # Store relative path
        avatar_url = f"/avatars/{filename}"
        set_avatar_user(db, user_id, avatar_url)

        print(f"Avatar uploaded: {filename} (User ID: {user_id})")
        return {"filename": filename, "avatar_url": avatar_url}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Upload failed: {str(e)}")

    finally:
        file.file.close()

@router.get("/user/avatar")
async def get_avatar(
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    user_id = current_user.get("user_id")
    avatar_url = get_avatar_user(db, user_id)

    if not avatar_url:
        raise HTTPException(status_code=404, detail="Avatar not found")

    file_path = os.path.join(AVATAR_DIR, os.path.basename(avatar_url))

    if not os.path.isfile(file_path):
        raise HTTPException(status_code=404, detail="Avatar file not found")

    return FileResponse(file_path)


# chưa làm update
@router.put("/user/update", response_model=Dict[str, Any])
def update_user_me(
        user_update: schemas.UserUpdate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    user_id = current_user.get("user_id")
    user_role = current_user.get("role")

    # Convert Pydantic model to dict and remove None values
    update_data = {k: v for k, v in user_update.dict().items() if v is not None}

    # Update the user
    updated_user = crud.update_user(db, user_id=user_id, user_data=update_data)
    if not updated_user:
        raise HTTPException(status_code=404, detail="User not found")

    return {"message": "User updated successfully"}


@router.get("/get_avatar")
async def get_avatar(
        db: Session = Depends(deps.get_db),
        avatar_url: Optional[str] = None
):
    file_path = os.path.join(AVATAR_DIR, os.path.basename(avatar_url))

    if not os.path.isfile(file_path):
        raise HTTPException(status_code=404, detail="Avatar file not found")

    return FileResponse(file_path)

