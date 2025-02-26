import { useState } from "react";

export default function LoginForm({ onClose, onSubmit }) {
  const [loginData, setLoginData] = useState({ name: "", email: "" });

  const handleChange = (e) => {
    setLoginData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(loginData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-xl shadow-lg max-w-md w-full">
        <h2 className="text-2xl font-semibold mb-4">Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700">Name</label>
            <input
              type="text"
              name="name"
              value={loginData.name}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your name"
            />
          </div>
          <div className="mb-4">
            <label className="block text-gray-700">Email</label>
            <input
              type="email"
              name="email"
              value={loginData.email}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your email"
            />
          </div>
          <button
            type="submit"
            className="bg-green-500 text-white px-6 py-2 rounded-lg hover:bg-green-600"
          >
            Submit
          </button>
        </form>
        <button
          onClick={onClose}
          className="mt-4 text-red-600 hover:text-red-800"
        >
          Close
        </button>
      </div>
    </div>
  );
}
