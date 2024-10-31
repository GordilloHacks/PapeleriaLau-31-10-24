-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 10-10-2024 a las 21:06:45
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `base_papeleria`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tabla1`
--

CREATE TABLE `tabla1` (
  `FechaHora` varchar(100) DEFAULT NULL,
  `ID` int(100) NOT NULL,
  `Categoria` varchar(1000) DEFAULT NULL,
  `Distribuidor` varchar(100) NOT NULL,
  `Producto` varchar(1000) DEFAULT NULL,
  `Cantidad` int(100) DEFAULT NULL,
  `Costo` varchar(1000) DEFAULT NULL,
  `Precio_Venta` varchar(1000) DEFAULT NULL,
  `Total_Gasto` varchar(100) DEFAULT NULL,
  `Total_Venta` varchar(100) DEFAULT NULL,
  `Descripcion` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tabla1`
--

INSERT INTO `tabla1` (`FechaHora`, `ID`, `Categoria`, `Distribuidor`, `Producto`, `Cantidad`, `Costo`, `Precio_Venta`, `Total_Gasto`, `Total_Venta`, `Descripcion`) VALUES
('03:10:53 a. m. | 10/10/2024', 17, 'Dulces', 'Aldor', 'SuperCoco Big', 7, '500', '1.500', '3.500', '10.500', '(sabor especial )'),
('03:10:59 a. m. | 10/10/2024', 18, 'Dulces', 'Aldor', 'Spakis', 6, '1.000', '2.500', '8.000', '20.000', '( sabor original )');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tablainvent`
--

CREATE TABLE `tablainvent` (
  `Producto` varchar(1000) DEFAULT NULL,
  `Stock_Inicial` varchar(1000) DEFAULT NULL,
  `Stock_Actual` varchar(1000) DEFAULT NULL,
  `Fecha_Entrada` varchar(1000) DEFAULT NULL,
  `Cant_Entrada` varchar(1000) DEFAULT NULL,
  `Fecha_Salida` varchar(1000) DEFAULT NULL,
  `Cant_Salida` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tablainvent`
--

INSERT INTO `tablainvent` (`Producto`, `Stock_Inicial`, `Stock_Actual`, `Fecha_Entrada`, `Cant_Entrada`, `Fecha_Salida`, `Cant_Salida`) VALUES
('SuperCoco Big', '10', '7', '03:10:53 a. m. | 10/10/2024', '0', '03:42:44 a. m. | 10/10/2024', '3'),
('Spakis', '10', '6', '03:10:59 a. m. | 10/10/2024', '0', '11:06:00 a. m. | 10/10/2024', '4');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tablaventas`
--

CREATE TABLE `tablaventas` (
  `IDs` int(100) NOT NULL,
  `Fecha` varchar(100) NOT NULL,
  `Categoria` varchar(100) NOT NULL,
  `Producto` varchar(100) NOT NULL,
  `Cantidad` varchar(100) NOT NULL,
  `Precio_Venta` varchar(100) NOT NULL,
  `Total_Venta` varchar(1000) NOT NULL,
  `Descripcion` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tablaventas`
--

INSERT INTO `tablaventas` (`IDs`, `Fecha`, `Categoria`, `Producto`, `Cantidad`, `Precio_Venta`, `Total_Venta`, `Descripcion`) VALUES
(40, '03:12:10 a. m. | 10/10/2024', 'Dulces', 'SuperCoco Big', '1', '1.500', '1.500', '(sabor especial )'),
(41, '03:26:14 a. m. | 10/10/2024', 'Dulces', 'Spakis', '1', '2.500', '2.500', '( sabor original )'),
(42, '03:27:47 a. m. | 10/10/2024', 'Dulces', 'SuperCoco Big', '1', '1.500', '1.500', '(sabor especial )'),
(43, '03:42:44 a. m. | 10/10/2024', 'Dulces', 'SuperCoco Big', '1', '1.500', '1.500', '(sabor especial )'),
(44, '04:15:41 a. m. | 10/10/2024', 'Dulces', 'Spakis', '1', '2.500', '2.500', '( sabor original )'),
(45, '11:06:00 a. m. | 10/10/2024', 'Dulces', 'Spakis', '2', '2.500', '5.000', '( sabor original )');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `tabla1`
--
ALTER TABLE `tabla1`
  ADD PRIMARY KEY (`ID`);

--
-- Indices de la tabla `tablaventas`
--
ALTER TABLE `tablaventas`
  ADD PRIMARY KEY (`IDs`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `tabla1`
--
ALTER TABLE `tabla1`
  MODIFY `ID` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `tablaventas`
--
ALTER TABLE `tablaventas`
  MODIFY `IDs` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
