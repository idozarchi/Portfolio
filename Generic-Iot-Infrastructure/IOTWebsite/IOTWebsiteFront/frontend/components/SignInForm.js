import { useState } from "react";

export default function SignInForm({ onClose, onSubmit }) {
  const [signInData, setSignInData] = useState({ name: "", email: "", company: "" });

  const handleChange = (e) => {
    setSignInData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(signInData);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-xl shadow-lg max-w-md w-full">
        <h2 className="text-2xl font-semibold mb-4">Sign Up</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700">Name</label>
            <input
              type="text"
              name="name"
              value={signInData.name}
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
              value={signInData.email}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your email"
            />
          </div>
          <div className="mb-4">
            <label className="block text-gray-700">Company's Token</label>
            <input
              type="text"
              name="company"
              value={signInData.company}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your company's token"
            />
          </div>
          <button type="submit" className="bg-green-500 text-white px-6 py-2 rounded-lg hover:bg-green-600">
            Submit
          </button>
        </form>
        <button onClick={onClose} className="mt-4 text-red-600 hover:text-red-800">
          Close
        </button>
      </div>
    </div>
  );
}
