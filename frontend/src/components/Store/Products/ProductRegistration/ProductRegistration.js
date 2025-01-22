import React, { useState } from 'react';
import './ProductRegistration.css';

function ProductRegistration() {
  const [formData, setFormData] = useState({
    prod_code: '',
    prod_name: '',
    prod_origin_price: '',
    prod_sale_price: '',
    prod_category: '',
    prod_category_detail: '',
    prod_desc: '',
    prod_main_img_path: '',
    prod_quantity: '',
    prod_sales_dist: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/products`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        alert('상품이 등록되었습니다.');
        setFormData({
          prod_code: '',
          prod_name: '',
          prod_origin_price: '',
          prod_sale_price: '',
          prod_category: '',
          prod_category_detail: '',
          prod_desc: '',
          prod_main_img_path: '',
          prod_quantity: '',
          prod_sales_dist: '',
        });
      } else {
        const error = await response.json();
        alert(error.message || '상품 등록에 실패했습니다.');
      }
    } catch (error) {
      alert('서버 오류가 발생했습니다.');
    }
  };

  return (
    <div className="product-registration-container">
      <h2>상품 등록</h2>
      <form onSubmit={handleSubmit}>
        <label>
          상품코드:
          <input type="text" name="prod_code" value={formData.prod_code} onChange={handleChange} required />
        </label>
        <label>
          상품명:
          <input type="text" name="prod_name" value={formData.prod_name} onChange={handleChange} required />
        </label>
        <label>
          원가:
          <input type="number" name="prod_origin_price" value={formData.prod_origin_price} onChange={handleChange} required />
        </label>
        <label>
          판매가:
          <input type="number" name="prod_sale_price" value={formData.prod_sale_price} onChange={handleChange} required />
        </label>
        <label>
          상품분류:
          <input type="text" name="prod_category" value={formData.prod_category} onChange={handleChange} required />
        </label>
        <label>
          상세분류:
          <input type="text" name="prod_category_detail" value={formData.prod_category_detail} onChange={handleChange} />
        </label>
        <label>
          상품설명:
          <textarea name="prod_desc" value={formData.prod_desc} onChange={handleChange} required></textarea>
        </label>
        <label>
          상품 이미지:
          <input type="file" name="prod_main_img_path" onChange={(e) => setFormData({ ...formData, prod_main_img_path: e.target.files[0] })} />
        </label>
        <label>
          재고:
          <input type="number" name="prod_quantity" value={formData.prod_quantity} onChange={handleChange} required />
        </label>
        <label>
          판매 여부:
          <select name="prod_sales_dist" value={formData.prod_sales_dist} onChange={handleChange}>
            <option value="yes">판매 중</option>
            <option value="no">판매 중지</option>
          </select>
        </label>
        <button type="submit">상품 등록</button>
      </form>
    </div>
  );
}

export default ProductRegistration;
