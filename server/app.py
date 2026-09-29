import sqlite3
import json
from flask import Flask, request, jsonify
from flask_sock import Sock

app = Flask(__name__)
sock = Sock(app)
DB_NAME = "zola_server.db"

active_websockets = {}

def init_db():
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL
        )
    ''')
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS messages (
            id TEXT PRIMARY KEY,
            sender TEXT NOT NULL,
            receiver TEXT NOT NULL,
            text TEXT NOT NULL,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    ''')
    conn.commit()
    conn.close()

# -----------------
# WEBSOCKET ROUTE (FULL-DUPLEX)
# -----------------
@sock.route('/ws/chat')
def chat_ws(ws):
    auth_msg = ws.receive()
    username = None
    try:
        # 1. Điểm danh
        data = json.loads(auth_msg)
        username = data.get("username")
        if username:
            active_websockets[username] = ws
            print(f"WebSocket connected: {username}")

        # 2. Vòng lặp nhận tin nhắn từ Client gửi lên
        while True:
            raw_msg = ws.receive()
            if not raw_msg: break

            msg_data = json.loads(raw_msg)
            msg_id = msg_data.get('id')
            sender = msg_data.get('sender')
            receiver = msg_data.get('receiver')
            text = msg_data.get('text')

            if msg_id and sender and receiver and text:
                # Lưu vào SQLite Server
                try:
                    conn = sqlite3.connect(DB_NAME)
                    cursor = conn.cursor()
                    cursor.execute('INSERT INTO messages (id, sender, receiver, text) VALUES (?, ?, ?, ?)',
                                   (msg_id, sender, receiver, text))
                    conn.commit()
                    conn.close()
                except sqlite3.IntegrityError:
                    pass # Bỏ qua nếu tin nhắn bị gửi trùng ID

                # Bắn tin nhắn trực tiếp xuống ống WebSocket của người nhận (nếu họ đang online)
                if receiver in active_websockets:
                    try:
                        payload = json.dumps({
                            'id': msg_id,
                            'sender': sender,
                            'receiver': receiver,
                            'text': text,
                            'timestamp': None
                        })
                        active_websockets[receiver].send(payload)
                        print(f"WS: Forwarded message from {sender} to {receiver}")
                    except Exception as e:
                        print(f"WS: Failed to forward message: {e}")

    except Exception as e:
        print(f"WebSocket Error: {e}")
    finally:
        if username and username in active_websockets:
            print(f"WebSocket disconnected: {username}")
            del active_websockets[username]

# -----------------
# API ROUTES (HTTP REST)
# -----------------
@app.route('/api/register', methods=['POST'])
def register():
    data = request.json or {}
    username = data.get('username')
    password = data.get('password')
    if not username or not password: return jsonify({'error': 'Vui lòng nhập đủ'}), 400
    try:
        conn = sqlite3.connect(DB_NAME)
        cursor = conn.cursor()
        cursor.execute('INSERT INTO users (username, password) VALUES (?, ?)', (username, password))
        conn.commit()
        conn.close()
        return jsonify({'message': 'Đăng ký thành công!'}), 201
    except sqlite3.IntegrityError:
        return jsonify({'error': 'Tài khoản đã tồn tại'}), 400

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
        return jsonify({'message': 'Đăng nhập thành công','token': f'token-{username}','username': username}), 200
    else:
        return jsonify({'error': 'Sai tên đăng nhập hoặc mật khẩu'}), 401

@app.route('/api/messages', methods=['GET'])
def get_messages():
    sender = request.args.get('sender')
    receiver = request.args.get('receiver')
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()
    cursor.execute('''
        SELECT id, sender, receiver, text, timestamp
        FROM messages
        WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)
        ORDER BY timestamp ASC
    ''', (sender, receiver, receiver, sender))
    rows = cursor.fetchall()
    conn.close()
    messages_list = [{'id': r[0], 'sender': r[1], 'receiver': r[2], 'text': r[3], 'timestamp': r[4]} for r in rows]
    return jsonify({'messages': messages_list}), 200

@app.route('/api/users', methods=['GET'])
def get_users():
    current_user = request.args.get('current_user')
    conn = sqlite3.connect(DB_NAME)
    cursor = conn.cursor()
    if current_user:
        cursor.execute('SELECT username FROM users WHERE username != ?', (current_user,))
    else:
        cursor.execute('SELECT username FROM users')
    rows = cursor.fetchall()
    conn.close()
    return jsonify({'users': [r[0] for r in rows]}), 200

if __name__ == '__main__':
    init_db()
    print("Server Flask (WebSocket) đang chạy tại cổng 5000...")
    app.run(host='0.0.0.0', port=5000, debug=True)
