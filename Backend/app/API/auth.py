from fastapi import APIRouter, Depends, HTTPException, status, Form
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from datetime import timedelta
from typing import Dict, Any

import crud
import schemas
from Oauth import deps
from Oauth.security import create_access_token

router = APIRouter(prefix="", tags=["authentication"])

@router.post("/login", response_model=schemas.Token)
def login_for_access_token(
        form_data: OAuth2PasswordRequestForm = Depends(),
        role: str = Form(default="Patient"),  # Cung cấp giá trị mặc định
        db: Session = Depends(deps.get_db)
):
    user = crud.authenticate_user(db, email=form_data.username, password=form_data.password)

    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )

    # Extract role from the user dictionary
    user_role = user["role"]

    if user_role != role:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect role",
            headers={"WWW-Authenticate": "Bearer"},
        )

    access_token_expires = timedelta(minutes=deps.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user["email"], "role": user_role, "user_id": user["user_id"], "full_name": user["full_name"] },
        expires_delta=access_token_expires
    )

    return {"access_token": access_token, "token_type": "bearer"}
