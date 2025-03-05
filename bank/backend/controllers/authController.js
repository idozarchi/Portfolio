const users = []; // Temporary in-memory storage (stub)

const signup = async (req, res) => {
    try {
        const { email, password, phone } = req.body;

        // Check if email already exists
        const existingUser = users.find(user => user.email === email);
        if (existingUser) return res.status(400).json({ message: "Email already exists" });

        // Create new mock user
        const newUser = { email, password, phone, isVerified: false };
        users.push(newUser);

        res.status(201).json({ message: "Signup successful! Please verify your phone.", user: newUser });
    } catch (error) {
        res.status(500).json({ message: "Server error", error: error.message });
    }
};

const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        // Find user in the mock database
        const user = users.find(user => user.email === email);
        if (!user) return res.status(400).json({ message: "User not found" });

        // Check if password matches (stub - no hashing for now)
        if (user.password !== password) return res.status(401).json({ message: "Invalid credentials" });

        res.status(200).json({ message: "Login successful!", user });
    } catch (error) {
        res.status(500).json({ message: "Server error", error: error.message });
    }
};

module.exports = { signup, login };
