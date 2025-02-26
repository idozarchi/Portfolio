import { useState } from 'react';

export default function RegisterCompanyForm({ onSubmit, onClose }) {
  const [registerData, setRegisterData] = useState({
    company_name: '',
    company_address: '',
    company_id: '',
    contact_id: '',
    email: '',
    contact_name: '',
    phone_number: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRegisterData((prevData) => ({
      ...prevData,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    onSubmit(registerData); // Pass the form data to the parent component's onSubmit function
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-xl shadow-lg max-w-md w-full">
        <h2 className="text-2xl font-semibold mb-4">Register Company</h2>
        <form id="os1" onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="companyName" className="block text-gray-700">Company Name</label>
            <input
              type="text"
              id="company_name"
              name="company_name"
              value={registerData.company_name}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your company's name"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="companyAddress" className="block text-gray-700">Company Address</label>
            <input
              type="text"
              id="company_address"
              name="company_address"
              value={registerData.company_address}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your company's address"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="companyID" className="block text-gray-700">Company ID</label>
            <input
              type="text"
              id="company_id"
              name="company_id"
              value={registerData.company_id}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your company's ID"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="username" className="block text-gray-700">Username</label>
            <input
              type="text"
              id="contact_id"
              name="contact_id"
              value={registerData.contact_id}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your username"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="email" className="block text-gray-700">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={registerData.email}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your email"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="contactName" className="block text-gray-700">Contact Name</label>
            <input
              type="text"
              id="contact_name"
              name="contact_name"
              value={registerData.contact_name}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter contact's name"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="phoneNumber" className="block text-gray-700">Phone Number</label>
            <input
              type="text"
              id="phone_number"
              name="phone_number"
              value={registerData.phone_number}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter contact's phone number"
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
