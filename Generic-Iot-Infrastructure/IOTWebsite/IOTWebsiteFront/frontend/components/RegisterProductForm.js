import { useState } from "react";

export default function RegisterProductForm({ onSubmit, onClose }) {
  const [registerData, setRegisterData] = useState({
    company_id: "",
    product_name: "",
    product_id: ""
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRegisterData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    onSubmit(registerData); // Pass the form data to the parent component's onSubmit function
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-xl shadow-lg max-w-md w-full">
        <h2 className="text-2xl font-semibold mb-4">Register Product</h2>
        <form id="os1" onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="companyID" className="block text-gray-700">
              Company ID
            </label>
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
            <label htmlFor="product_name" className="block text-gray-700">
              Product Name
            </label>
            <input
              type="text"
              id="product_name"
              name="product_name"
              value={registerData.product_name}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your product name"
            />
          </div>

          <div className="mb-4">
            <label htmlFor="product_id" className="block text-gray-700">
              Product ID
            </label>
            <input
              type="text"
              id="product_id"
              name="product_id"
              value={registerData.product_id}
              onChange={handleChange}
              className="mt-2 px-4 py-2 border border-gray-300 rounded-lg w-full"
              placeholder="Enter your product id"
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
