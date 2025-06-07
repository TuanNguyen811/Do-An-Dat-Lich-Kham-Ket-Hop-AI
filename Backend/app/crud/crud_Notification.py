from sqlalchemy import text
from sqlalchemy.orm import Session

import schemas
import crud
from typing import Optional, Dict, Any, List


# Notification CRUD operations
def get_notification1(db: Session, notification_id: int):
    query = text("SELECT * FROM Notifications WHERE notification_id = :notification_id")
    result = db.execute(query, {"notification_id": notification_id}).first()
    return result


def get_notifications(db: Session, user_id: int = None, skip: int = 0, limit: int = 100) -> List[dict]:
    if user_id:
        query = text("""
            SELECT * FROM Notifications 
            WHERE user_id = :user_id 
            LIMIT :limit OFFSET :skip
        """)
        result = db.execute(query, {"user_id": user_id, "skip": skip, "limit": limit}).fetchall()
    else:
        query = text("SELECT * FROM Notifications LIMIT :limit OFFSET :skip")
        result = db.execute(query, {"skip": skip, "limit": limit}).fetchall()

    notifications = [
        {
            "notification_id": row[0],
            "user_id": row[1],
            "type": row[2],
            "message": row[3],
            "scheduled_time": row[4],
            "status": row[5],
            "created_at": row[6]
        } for row in result
    ]

    return notifications

def get_notification(db: Session, notification_id: int):
    query = text("SELECT * FROM Notifications WHERE notification_id = :notification_id")
    result = db.execute(query, {"notification_id": notification_id}).first()
    if not result:
        return None
    return {
        "notification_id": result[0],
        "user_id": result[1],
        "type": result[2],
        "message": result[3],
        "scheduled_time": result[4],
        "status": result[5],
        "created_at": result[6]
    }

def create_notification(db: Session, notification: schemas.NotificationCreate):
    query = text("""
        INSERT INTO Notifications (user_id, type, message, scheduled_time)
        VALUES (:user_id, :type, :message, :scheduled_time)
    """)

    result = db.execute(
        query,
        {
            "user_id": notification.user_id,
            "type": notification.type,
            "message": notification.message,
            "scheduled_time": notification.scheduled_time
        }
    )
    db.commit()

    # Get the last inserted ID
    # result = db.execute(text("SELECT LAST_INSERT_ID()")).scalar()

    #return get_notification(db, result)
    return result


def update_notification(db: Session, notification_id: int, notification_data: Dict[str, Any]):
    # First check if notification exists
    notification = get_notification(db, notification_id)
    if not notification:
        return None

    # Prepare update parts
    update_parts = []
    params = {"notification_id": notification_id}

    valid_fields = ["message", "scheduled_time", "status"]

    for key, value in notification_data.items():
        if key in valid_fields:
            update_parts.append(f"{key} = :{key}")
            params[key] = value

    if not update_parts:
        return notification

    # Build and execute update query
    query = text(f"""
        UPDATE Notifications
        SET {', '.join(update_parts)}
        WHERE notification_id = :notification_id
    """)

    db.execute(query, params)
    db.commit()

    return get_notification(db, notification_id)


def delete_notification(db: Session, notification_id: int):
    # First get the notification to return it
    notification = get_notification(db, notification_id)
    if not notification:
        return None

    # Delete the notification
    query = text("DELETE FROM Notifications WHERE notification_id = :notification_id")
    db.execute(query, {"notification_id": notification_id})
    db.commit()

    return notification