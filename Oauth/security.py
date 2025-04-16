from passlib.context import CryptContext
from jose import jwt
from datetime import datetime, timedelta
'''
Cung cấp các hàm để xác thực mật khẩu, tạo mã băm mật khẩu và tạo mã thông báo truy cập.
'''

# Consider using environment variables for these in production
SECRET_KEY = "43928492375778943758926372907"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 400

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password):
    return pwd_context.hash(password)

def create_access_token(data: dict, expires_delta: timedelta = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt