import React, { useState, useEffect } from 'react';

function ProductList() {
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetch('/api/stores', { method: 'GET' }) // 'GET' 메서드 명시적으로 추가
      .then((res) => res.json())
      .then((data) => {
        console.log('API 응답 데이터:', data); // 응답 데이터 확인
        setProducts(Array.isArray(data) ? data : []); // 배열인지 확인 후 설정
      })
      .catch((error) => console.error('상품 데이터 불러오기 실패:', error));
  }, []);

  // 검색 필터링: 상품명, 카테고리, 제조사 ID 기준
  const filteredProducts = products.filter((product) => {
    const lowerSearchTerm = searchTerm.toLowerCase(); // 검색어를 소문자로 변환
    return (
      product.productName.toLowerCase().includes(lowerSearchTerm) || // 상품명
      product.category.toLowerCase().includes(lowerSearchTerm) || // 카테고리
      product.manufacturerId.toLowerCase().includes(lowerSearchTerm) // 제조사 ID
    );
  });



  return (
    <div>
      <h2>상품 목록</h2>
      {/* 검색 입력 필드 */}
      <input
        type="text"
        placeholder="상품명, 카테고리, 제조사 ID 검색"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        style={{ marginBottom: '20px', padding: '5px', width: '300px' }}
      />

      <table>
        <thead>
          <tr>
            <th>상품 코드</th>
            <th>상품명</th>
            <th>카테고리</th>
            <th>상세 카테고리</th>
            <th>설명</th>
            <th>이미지</th>
            <th>판매가</th>
            <th>재고</th>
            <th>제조사 ID</th>
          </tr>
        </thead>
        <tbody>
          {filteredProducts.length > 0 ? (
            filteredProducts.map((product) => (
              <tr key={product.productCode}>
                <td>{product.productCode}</td>
                <td>{product.productName}</td>
                <td>{product.category}</td>
                <td>{product.subCategory}</td>
                <td>{product.description}</td>
           <td>
             {product.imageUrl ? (
               <img
                 src={`${process.env.REACT_APP_API_BASE_URL}/api/stores/images/${product.imageUrl.split('/').pop()}`}
                 alt={product.productName}
                 style={{ width: '120px', height: '120px', objectFit: 'cover' }}
               />
             ) : (
               <img
                 src="/images/default_product.jpg" // 기본 이미지
                 alt="기본 이미지"
                 style={{ width: '120px', height: '120px', objectFit: 'cover' }}
               />
             )}
           </td>
                <td>{product.salePrice}</td>
                <td>{product.stock}</td>
                <td>{product.manufacturerId}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="9">검색 결과가 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default ProductList;
