from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import pydantic
import codecs


DATABASE_URL = "mysql+pymysql://root:1q2w3e4R5613!@localhost/medicalscheduling"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

def create_tables():
    from sqlalchemy import create_engine, text

    # Replace 'your_database_url' with your actual database URL
    engine = create_engine(DATABASE_URL)

    with engine.connect() as conn:
        # Fix the file path to match your project structure
        with codecs.open('database/create_tables.sql', 'r', encoding='utf-8', errors='replace') as f:
            try:
                sql_script = f.read()
            except Exception as e:
                print("Error: ", e)
                return

        # Split the script by semicolons to execute each statement
        statements = sql_script.split(';')
        for statement in statements:
            if statement.strip():
                conn.execute(text(statement))
    #   conn.commit()