from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import Dict, Any, List

import crud
import schemas
from Oauth import deps

router = APIRouter(prefix="", tags=["notifications"])


# Notification routes
@router.post("/notifications", response_model=Dict[str, Any])
def create_notification(
        notification: schemas.NotificationCreate,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Only admins and doctors can create notifications
    if current_user.get("role") not in ["Admin", "Doctor"]:
        raise HTTPException(status_code=403, detail="Not authorized to create notifications")

    # Check if the user exists
    user = crud.get_user(db, user_id=notification.user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    # Create notification
    result = crud.create_notification(db=db, notification=notification)
    if not result:
        raise HTTPException(status_code=500, detail="Failed to create notification")

    return {"message": "Notification created successfully", "notification_id": result[0]}

@router.post("/notifications/list", response_model=Dict[str, Any])
def create_notifications(
        notifications: List[schemas.NotificationCreate],
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Only admins and doctors can create notifications
    if current_user.get("role") not in ["Admin", "Doctor"]:
        raise HTTPException(status_code=403, detail="Not authorized to create notifications")

    for notification in notifications:
        # Check if the user exists
        user = crud.get_user(db, user_id=notification.user_id)
        if not user:
            raise HTTPException(status_code=404, detail=f"User with ID {notification.user_id} not found")

        # Create notification
        result = crud.create_notification(db=db, notification=notification)
        if not result:
            raise HTTPException(status_code=500, detail=f"Failed to create notification for user ID {notification.user_id}")


    return {"message": "Notification created successfully"}

@router.get("/notifications", response_model=List[Dict[str, Any]])
def get_notifications(
        skip: int = 0,
        limit: int = 100,
        current_user: Dict[str, Any] = Depends(deps.get_current_user),
        db: Session = Depends(deps.get_db)
):
    # Users can only see their own notifications
    user_id = current_user.get("user_id")

    notifications = crud.get_notifications(db, user_id=user_id, skip=skip, limit=limit)
    return notifications


