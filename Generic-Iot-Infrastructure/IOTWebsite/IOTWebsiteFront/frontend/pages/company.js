import { useEffect, useState } from "react";

import Button from "../components/Button";
import RegisterProductForm from "../components/RegisterProductForm"; // Import the component

export default function CompanyPage() {
  const [products, setProducts] = useState([]);
  const [showRegisterForm, setShowRegisterForm] = useState(false); // Control modal visibility
  const companyID = "1"; // Replace with the actual logged-in user's company ID.

  useEffect(() => {
    fetch(
      `http://localhost:8080/IOTsWebsite/companyProducts?companyID=${companyID}`
    )
      .then((response) => response.json())
      .then((data) => {
        console.log("Fetched Products:", data);
        setProducts(data);
      })
      .catch((error) => console.error("Error fetching products:", error));
  }, [companyID]);

  const handleRegisterSubmit = async (productData) => {
    console.log("Registering Product:", productData);
    setShowRegisterForm(false); // Close the form after submission
  
    let data;
    let url;

    data = productData;
    url = "http://localhost:8080/IOTsWebsite/product";
    console.log(data);

    try {
      const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        alert("Form submitted successfully!");
        setShowRegisterForm(false);
        
      } else {
        alert("Failed to submit form.");
      }
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("Error submitting form.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-gray-300 to-gray-600 text-black">
      <section className="flex flex-col items-center justify-center text-center py-24 px-4">
        <h1 className="text-5xl font-extrabold leading-tight mb-4 drop-shadow-lg">
          Company Dashboard
        </h1>
        <p className="text-xl mb-8 max-w-3xl mx-auto">
          Welcome to your company’s dashboard. Manage your data and devices here!
        </p>

        {/* Register Product Button */}
        <Button label="Register Product" onClick={() => setShowRegisterForm(true)} />

        {/* Products Table */}
        {products.length > 0 ? (
          <div className="overflow-x-auto w-full max-w-4xl bg-white shadow-md rounded-lg p-4 mt-7 mb-4 mx-5">
            <table className="w-full border-collapse ">
              <thead>
                <tr className="bg-gray-800 text-white">
                  <th className="py-2 px-4 border">Product Name</th>
                  <th className="py-2 px-4 border">Product ID</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product, index) => (
                  <tr key={index} className="text-center bg-gray-100 hover:bg-gray-200">
                    <td className="py-2 px-4 border">{product.ProductName || "N/A"}</td>
                    <td className="py-2 px-4 border">{product.ProductID || "N/A"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-lg text-gray-700">No products found for this company.</p>
        )}
      </section>

      {/* Register Product Form Modal */}
      {showRegisterForm && (
        <RegisterProductForm 
          onSubmit={handleRegisterSubmit} 
          onClose={() => setShowRegisterForm(false)} 
        />
      )}
    </div>
  );
}
