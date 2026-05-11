/* ============================
   StegaCrypt — App Logic
   LSB Steganography Engine
   ============================ */

// ==================== MATRIX BACKGROUND ====================
function initMatrixBackground() {
    const canvas = document.getElementById('matrix-bg');
    const ctx = canvas.getContext('2d');

    function resize() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    resize();
    window.addEventListener('resize', resize);

    const chars = '01';
    const fontSize = 14;
    const columns = Math.floor(canvas.width / fontSize);
    const drops = Array(columns).fill(1);

    function draw() {
        ctx.fillStyle = 'rgba(6, 6, 15, 0.08)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = '#00d4ff';
        ctx.font = `${fontSize}px 'JetBrains Mono', monospace`;

        for (let i = 0; i < drops.length; i++) {
            const text = chars[Math.floor(Math.random() * chars.length)];
            ctx.fillText(text, i * fontSize, drops[i] * fontSize);
            if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
                drops[i] = 0;
            }
            drops[i]++;
        }
        requestAnimationFrame(draw);
    }
    draw();
}

// ==================== STEGANOGRAPHY ENGINE ====================
const StegoEngine = {
    /**
     * Encode a message into image data using LSB encoding.
     * Format: [32-bit message length][8-bit per char message data]
     */
    encode(imageData, message) {
        const data = imageData.data;
        const binaryMessage = this._messageToBinary(message);
        const lengthBinary = message.length.toString(2).padStart(32, '0');
        const fullBinary = lengthBinary + binaryMessage;
        const maxBits = this._getCapacity(data.length);

        if (fullBinary.length > maxBits) {
            throw new Error(`Message too long! Max ${Math.floor((maxBits - 32) / 8)} characters for this image.`);
        }

        let bitIndex = 0;
        let pixelsModified = 0;
        const modifiedBits = [];

        for (let i = 0; i < data.length && bitIndex < fullBinary.length; i++) {
            // Skip alpha channel (every 4th byte)
            if ((i + 1) % 4 === 0) continue;

            const originalBit = data[i] & 1;
            const newBit = parseInt(fullBinary[bitIndex]);
            data[i] = (data[i] & 0xFE) | newBit;

            if (i % 4 === 0) pixelsModified++;

            modifiedBits.push({
                index: i,
                channel: ['R', 'G', 'B'][i % 4],
                originalBit,
                newBit,
                modified: originalBit !== newBit
            });

            bitIndex++;
        }

        return {
            imageData,
            stats: {
                messageLength: message.length,
                bitsEncoded: fullBinary.length,
                pixelsModified: Math.ceil(fullBinary.length / 3),
                totalPixels: data.length / 4,
                capacity: Math.floor((maxBits - 32) / 8),
                usage: ((fullBinary.length / maxBits) * 100).toFixed(2)
            },
            modifiedBits
        };
    },

    /**
     * Decode a hidden message from image data.
     */
    decode(imageData) {
        const data = imageData.data;
        let binary = '';

        // Read all LSBs (skip alpha)
        for (let i = 0; i < data.length; i++) {
            if ((i + 1) % 4 === 0) continue;
            binary += (data[i] & 1).toString();
        }

        // Extract length (first 32 bits)
        const length = parseInt(binary.substring(0, 32), 2);

        // Validate
        if (length <= 0 || length > 100000) {
            throw new Error('No valid hidden message found in this image.');
        }

        const totalBits = 32 + length * 8;
        if (totalBits > binary.length) {
            throw new Error('Image data is corrupted or doesn\'t contain a valid message.');
        }

        // Extract message
        const messageBinary = binary.substring(32, totalBits);
        let message = '';
        for (let i = 0; i < messageBinary.length; i += 8) {
            const charCode = parseInt(messageBinary.substring(i, i + 8), 2);
            if (charCode === 0) break;
            message += String.fromCharCode(charCode);
        }

        // Validate readable text
        if (!/^[\x20-\x7E\n\r\t]+$/.test(message)) {
            throw new Error('No valid hidden message found in this image.');
        }

        return message;
    },

    _messageToBinary(message) {
        return message.split('').map(char =>
            char.charCodeAt(0).toString(2).padStart(8, '0')
        ).join('');
    },

    _getCapacity(dataLength) {
        // Total usable channels (R, G, B — skip alpha)
        return Math.floor(dataLength * 3 / 4);
    }
};

// ==================== UI CONTROLLER ====================
document.addEventListener('DOMContentLoaded', () => {
    initMatrixBackground();

    // Elements
    const encodeTab = document.getElementById('encode-tab');
    const decodeTab = document.getElementById('decode-tab');
    const modeToggle = document.querySelector('.mode-toggle');
    const encodePanel = document.getElementById('encode-panel');
    const decodePanel = document.getElementById('decode-panel');

    const encodeDropzone = document.getElementById('encode-dropzone');
    const encodeFileInput = document.getElementById('encode-file-input');
    const encodePreview = document.getElementById('encode-preview');
    const secretMessage = document.getElementById('secret-message');
    const charCount = document.getElementById('char-count');
    const maxChars = document.getElementById('max-chars');
    const encodeBtn = document.getElementById('encode-btn');

    const decodeDropzone = document.getElementById('decode-dropzone');
    const decodeFileInput = document.getElementById('decode-file-input');
    const decodePreview = document.getElementById('decode-preview');
    const decodeBtn = document.getElementById('decode-btn');

    let encodeImage = null;
    let decodeImage = null;

    // ---- Mode Toggle ----
    encodeTab.addEventListener('click', () => switchMode('encode'));
    decodeTab.addEventListener('click', () => switchMode('decode'));

    function switchMode(mode) {
        modeToggle.setAttribute('data-active', mode);
        encodeTab.classList.toggle('active', mode === 'encode');
        decodeTab.classList.toggle('active', mode === 'decode');
        encodePanel.classList.toggle('active', mode === 'encode');
        decodePanel.classList.toggle('active', mode === 'decode');
    }

    // ---- Dropzone Handlers ----
    function setupDropzone(dropzone, fileInput, previewImg, callback) {
        dropzone.addEventListener('click', () => fileInput.click());

        dropzone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropzone.classList.add('dragover');
        });

        dropzone.addEventListener('dragleave', () => {
            dropzone.classList.remove('dragover');
        });

        dropzone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropzone.classList.remove('dragover');
            const file = e.dataTransfer.files[0];
            if (file && file.type.startsWith('image/')) {
                handleFile(file, dropzone, previewImg, callback);
            }
        });

        fileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) handleFile(file, dropzone, previewImg, callback);
        });
    }

    function handleFile(file, dropzone, previewImg, callback) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const img = new Image();
            img.onload = () => {
                previewImg.src = e.target.result;
                previewImg.classList.remove('hidden');
                dropzone.querySelector('.dropzone-content').classList.add('hidden');
                callback(img);
            };
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }

    setupDropzone(encodeDropzone, encodeFileInput, encodePreview, (img) => {
        encodeImage = img;
        updateEncodeState();
    });

    setupDropzone(decodeDropzone, decodeFileInput, decodePreview, (img) => {
        decodeImage = img;
        decodeBtn.disabled = false;
    });

    // ---- Message Input ----
    secretMessage.addEventListener('input', () => {
        charCount.textContent = secretMessage.value.length;
        updateEncodeState();
    });

    function updateEncodeState() {
        if (encodeImage) {
            const canvas = document.createElement('canvas');
            canvas.width = encodeImage.width;
            canvas.height = encodeImage.height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(encodeImage, 0, 0);
            const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
            const capacity = Math.floor((StegoEngine._getCapacity(imageData.data.length) - 32) / 8);
            maxChars.textContent = capacity.toLocaleString();
        }
        encodeBtn.disabled = !encodeImage || !secretMessage.value.trim();
    }

    // ---- Encode ----
    encodeBtn.addEventListener('click', () => {
        if (!encodeImage || !secretMessage.value.trim()) return;

        encodeBtn.classList.add('loading');
        encodeBtn.textContent = 'Encoding...';

        setTimeout(() => {
            try {
                const canvas = document.createElement('canvas');
                canvas.width = encodeImage.width;
                canvas.height = encodeImage.height;
                const ctx = canvas.getContext('2d');
                ctx.drawImage(encodeImage, 0, 0);
                const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

                const result = StegoEngine.encode(imageData, secretMessage.value);

                // Draw result
                ctx.putImageData(result.imageData, 0, 0);

                const resultCanvas = document.getElementById('result-canvas');
                resultCanvas.width = canvas.width;
                resultCanvas.height = canvas.height;
                const resultCtx = resultCanvas.getContext('2d');
                resultCtx.putImageData(result.imageData, 0, 0);

                // Update stats
                document.getElementById('stat-msg-len').textContent = result.stats.messageLength + ' chars';
                document.getElementById('stat-bits').textContent = result.stats.bitsEncoded.toLocaleString();
                document.getElementById('stat-pixels').textContent = result.stats.pixelsModified.toLocaleString();
                document.getElementById('stat-capacity').textContent = result.stats.capacity.toLocaleString() + ' chars';
                document.getElementById('stat-usage').textContent = result.stats.usage + '%';

                // Binary visualization
                renderBinaryViz(result.modifiedBits, result.imageData);

                // Show result
                document.getElementById('encode-result').classList.remove('hidden');

                // Download button
                document.getElementById('download-btn').onclick = () => {
                    const link = document.createElement('a');
                    link.download = 'stegacrypt_encoded.png';
                    link.href = resultCanvas.toDataURL('image/png');
                    link.click();
                };

            } catch (err) {
                alert('Error: ' + err.message);
            } finally {
                encodeBtn.classList.remove('loading');
                encodeBtn.innerHTML = `
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                    Encode Message
                `;
            }
        }, 100);
    });

    // ---- Decode ----
    decodeBtn.addEventListener('click', () => {
        if (!decodeImage) return;

        decodeBtn.classList.add('loading');
        decodeBtn.textContent = 'Decoding...';

        setTimeout(() => {
            try {
                const canvas = document.createElement('canvas');
                canvas.width = decodeImage.width;
                canvas.height = decodeImage.height;
                const ctx = canvas.getContext('2d');
                ctx.drawImage(decodeImage, 0, 0);
                const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

                const message = StegoEngine.decode(imageData);

                document.querySelector('.decode-placeholder').classList.add('hidden');
                const msgWrap = document.getElementById('decoded-message-wrap');
                msgWrap.classList.remove('hidden');
                document.getElementById('decoded-message').textContent = message;

            } catch (err) {
                alert('Error: ' + err.message);
            } finally {
                decodeBtn.classList.remove('loading');
                decodeBtn.innerHTML = `
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 5-5 5 5 0 0 1 5 5v4"/></svg>
                    Decode Message
                `;
            }
        }, 100);
    });

    // ---- Binary Visualization ----
    function renderBinaryViz(modifiedBits, imageData) {
        const grid = document.getElementById('pixel-grid');
        grid.innerHTML = '';

        const pixelCount = Math.min(64, Math.floor(modifiedBits.length / 3));

        for (let p = 0; p < pixelCount; p++) {
            const baseIdx = p * 3;
            const pixelIdx = Math.floor(modifiedBits[baseIdx].index / 4) * 4;

            const r = imageData.data[pixelIdx];
            const g = imageData.data[pixelIdx + 1];
            const b = imageData.data[pixelIdx + 2];

            const cell = document.createElement('div');
            cell.className = 'pixel-cell';

            const colorBox = document.createElement('div');
            colorBox.className = 'pixel-color';
            colorBox.style.background = `rgb(${r},${g},${b})`;

            const bitsDiv = document.createElement('div');
            bitsDiv.className = 'pixel-bits';

            let bitsHTML = `P${p}: `;
            for (let c = 0; c < 3 && baseIdx + c < modifiedBits.length; c++) {
                const bit = modifiedBits[baseIdx + c];
                const val = ['R', 'G', 'B'][c] + ':' + bit.newBit;
                if (bit.modified) {
                    bitsHTML += `<span class="modified">${val}</span> `;
                } else {
                    bitsHTML += val + ' ';
                }
            }

            bitsDiv.innerHTML = bitsHTML;
            cell.appendChild(colorBox);
            cell.appendChild(bitsDiv);
            grid.appendChild(cell);
        }
    }

    // ---- Smooth scroll for nav ----
    document.querySelectorAll('a[href^="#"]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = document.querySelector(link.getAttribute('href'));
            if (target) target.scrollIntoView({ behavior: 'smooth' });
        });
    });
});
