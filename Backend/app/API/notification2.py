from apscheduler.schedulers.background import BackgroundScheduler
from datetime import datetime
from sqlalchemy.orm import Session
from fastapi import FastAPI
import crud
from database import SessionLocal

app = FastAPI()

# Function to send notifications
def send_pending_notifications():
    db: Session = SessionLocal()
    try:
        # Get all pending notifications with scheduled_time <= now
        now = datetime.now()
        pending_notifications = get_due_pending_notifications(db)

        for notification in pending_notifications:
            if notification["status"] == "Pending" and notification["scheduled_time"] <= now:
                # Simulate sending the notification (e.g., via email or SMS)
                try:
                    print(f"Sending notification: {notification['message']}")
                    # Update status to 'Sent'
                    crud.update_notification(
                        db, notification_id=notification["notification_id"], notification_data={"status": "Sent"}
                    )
                except Exception as e:
                    print(f"Failed to send notification: {e}")
                    # Update status to 'Failed'
                    crud.update_notification(
                        db, notification_id=notification["notification_id"], notification_data={"status": "Failed"}
                    )
    except Exception as e:
        print(f"Error during notification processing: {e}")
    finally:
        db.close()

# Function to get pending notifications
def get_due_pending_notifications(db: Session):
    try:
        return db.execute(
            """
            SELECT * FROM Notifications
            WHERE status = 'Pending' AND scheduled_time <= NOW()
            """
        ).mappings().all()
    except Exception as e:
        print(f"Error fetching pending notifications: {e}")
        return []

# Initialize the scheduler
scheduler = BackgroundScheduler()
scheduler.add_job(send_pending_notifications, "interval", seconds=200)  # Run every 60 seconds
scheduler.start()

# Shutdown the scheduler when the app stops
@app.on_event("shutdown")
def shutdown_scheduler():
    scheduler.shutdown()