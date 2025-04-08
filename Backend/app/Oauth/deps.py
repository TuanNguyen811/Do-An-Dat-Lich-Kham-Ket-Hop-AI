from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import JWTError, jwt
from sqlalchemy import text
from sqlalchemy.orm import Session

from database.database import SessionLocal
from Oauth.security import SECRET_KEY, ALGORITHM, verify_password

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")
ACCESS_TOKEN_EXPIRE_MINUTES = 3000


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def get_current_user(db: Session = Depends(get_db), token: str = Depends(oauth2_scheme)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        email: str = payload.get("sub")
        role: str = payload.get("role")
        user_id: int = payload.get("user_id")
        full_name: str = payload.get("full_name")

        if email is None or role is None or user_id is None:
            raise credentials_exception

    except JWTError:
        raise credentials_exception

    # Return user info from the token instead of querying the database
    # This is more efficient and allows us to skip an extra database query
    return {
        "email": email,
        "role": role,
        "user_id": user_id,
        "full_name": full_name
    }


