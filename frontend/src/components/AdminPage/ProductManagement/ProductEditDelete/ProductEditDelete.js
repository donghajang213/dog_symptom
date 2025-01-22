import React, { useState, useEffect } from 'react';

function ProductDelete() {
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetch('/api/stores', { method: 'GET' }) // 상품 목록 불러오기
      .then((res) => res.json())
      .then((data) => setProducts(Array.isArray(data) ? data : []))
      .catch((error) => console.error('상품 데이터 불러오기 실패:', error));
  }, []);

  const handleDelete = (productCode) => {
    if (window.confirm('정말로 삭제하시겠습니까?')) {
      fetch(`/api/stores/${productCode}`, { method: 'DELETE' }) // DELETE 요청
        .then((res) => {
          if (res.ok) {
            alert('상품이 삭제되었습니다.');
            setProducts(products.filter((product) => product.productCode !== productCode));
          } else {
            alert('상품 삭제 실패!');
          }
        })
        .catch((error) => console.error('상품 삭제 실패:', error));
    }
  };

  // 검색어에 따른 필터링
  const filteredProducts = products.filter(
    (product) =>
      product.productCode.includes(searchTerm) ||
      product.productName.includes(searchTerm) ||
      product.manufacturerId.includes(searchTerm)
  );

  return (
    <div>
      <h2>상품 삭제</h2>
      <div>
        <input
          type="text"
          placeholder="검색 (상품 코드, 상품명, 제조사 ID)"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>
      <table>
        <thead>
          <tr>
            <th>상품 코드</th>
            <th>상품명</th>
            <th>상품 가격</th>
            <th>제조사 ID</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          {Array.isArray(filteredProducts) && filteredProducts.length > 0 ? (
            filteredProducts.map((product) => (
              <tr key={product.productCode}>
                <td>{product.productCode}</td>
                <td>{product.productName}</td>
                <td>{product.salePrice}</td>
                <td>{product.manufacturerId}</td>
                <td>
                  <button onClick={() => handleDelete(product.productCode)}>삭제</button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="5">데이터가 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default ProductDelete;
