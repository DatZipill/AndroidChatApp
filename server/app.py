import sqlite3
from flask import Flask, request, jsonify

app = Flask(__name__)

DB_NAME = "zola_server.db"

def init_db():
    """Khởi tạo các bảng dữ liệu trong SQLite server"""
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()

    # Bảng người dùng
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL
        )
    ''')

    # Bảng tin nhắn trên Server
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS messages (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            sender TEXT NOT NULL,
            receiver TEXT NOT NULL,
            text TEXT NOT NULL,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    ''')

    conn.commit()
    conn.close()

# ----------------------------------------------------
# API 1: Đăng ký tài khoản
# ----------------------------------------------------
@app.route('/api/register', methods=['POST'])
def register():
    data = request.json or {}
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({'error': 'Vui lòng nhập đủ username và password'}), 400

    try:
        conn = sqlite3.connect(DB_NAME)
        cursor = conn.cursor()
        cursor.execute('INSERT INTO users (username, password) VALUES (?, ?)', (username, password))
        conn.commit()
        conn.close()
        return jsonify({'message': 'Đăng ký thành công!'}), 201
    except sqlite3.IntegrityError:
        return jsonify({'error': 'Tài khoản đã tồn tại'}), 400

# ----------------------------------------------------
# API 2: Đăng nhập
# ----------------------------------------------------
@app.route('/api/login', methods=['POST'])
def login():
    data = request.json or {}
    username = data.get('username')
    password = data.get('password')

    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()
    cursor.execute('SELECT * FROM users WHERE username = ? AND password = ?', (username, password))
    user = cursor.fetchone()
    conn.close()

    if user:
        # Trả về token giả định đơn giản (dựa vào username)
        return jsonify({
            'message': 'Đăng nhập thành công',
            'token': f'fake-jwt-token-for-{username}',
            'username': username
        }), 200
    else:
        return jsonify({'error': 'Sai tên đăng nhập hoặc mật khẩu'}), 401

if __name__ == '__main__':
    init_db()
    print("Server Flask đang chạy tại cổng 5000...")
    app.run(host='0.0.0.0', port=5000, debug=True)
